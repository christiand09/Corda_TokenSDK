package quantum.userTransaction

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.UserContract
import com.quantum.states.UserIssueState
import com.quantum.states.UserState
import com.r3.corda.lib.tokens.contracts.types.TokenType
import com.r3.corda.lib.tokens.money.FiatCurrency
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class UserTransferTokenFlow(    private val issueId: String,
                                private val platFormId: String) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        return recordTransaction(signedTransaction)
    }

    private fun inputState(): StateAndRef<UserState> {
        val platFormID = UniqueIdentifier.fromString(platFormId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(platFormID))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }

    private fun userInputState(): StateAndRef<UserState> {
        val userId = inputIssueState().state.data.owner
        val userID = UniqueIdentifier.fromString(userId.toString())
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(userID))
        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
    }

    private fun inputIssueState(): StateAndRef<UserIssueState> {
        val issueID = UniqueIdentifier.fromString(issueId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(issueID))
        return serviceHub.vaultService.queryBy<UserIssueState>(queryCriteria).states.single()
    }

    private fun outputState(): UserState {
        val currency = inputIssueState().state.data.issueToken.token.tokenIdentifier
        val amount = inputIssueState().state.data.issueToken.quantity
        val token = FiatCurrency.getInstance(currency)
        val inputState = inputState().state.data

        val balanceCurrencyList = mutableListOf<Amount<TokenType>>()
        val sentCurrencyList = mutableListOf<Amount<TokenType>>()

        for (x in inputState.balance){
            if (x.token.tokenIdentifier == currency){
                val total = x.quantity - amount
                val amount = Amount(total,token)
                balanceCurrencyList.add(amount)
            }else{
                balanceCurrencyList.add(x)
            }
        }
        for (x in inputState.totalSent){
            if (x.token.tokenIdentifier == currency){
                val total = x.quantity + amount
                val amount = Amount(total,token)
                sentCurrencyList.add(amount)
            }else{
                sentCurrencyList.add(x)
            }
        }

        return inputState.transfer(balanceCurrencyList,inputState.totalReceived,sentCurrencyList)
    }

    private fun userOutputState(): UserState {
        val currency = inputIssueState().state.data.issueToken.token.tokenIdentifier
        val amount = inputIssueState().state.data.issueToken.quantity
        val token = FiatCurrency.getInstance(currency)
        val inputState = userInputState().state.data

        val balanceCurrencyList = mutableListOf<Amount<TokenType>>()
        val receiveCurrencyList = mutableListOf<Amount<TokenType>>()

        for (x in inputState.balance){
            if (x.token.tokenIdentifier == currency){
                val total = x.quantity + amount
                val amount = Amount(total,token)
                balanceCurrencyList.add(amount)
            }else{
                balanceCurrencyList.add(x)
            }
        }
        for (x in inputState.totalReceived){
            if (x.token.tokenIdentifier == currency){
                val total = x.quantity + amount
                val amount = Amount(total,token)
                receiveCurrencyList.add(amount)
            }else{
                receiveCurrencyList.add(x)
            }
        }

        return inputState.transfer(balanceCurrencyList,receiveCurrencyList,inputState.totalSent)
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(UserContract.Commands.Register(), ourIdentity.owningKey)
        val builder = TransactionBuilder(notary)
        builder.addInputState(userInputState())
        builder.addInputState(inputState())
        builder.addOutputState(outputState(), UserContract.USER_CONTRACT_ID)
        builder.addOutputState(userOutputState(), UserContract.USER_CONTRACT_ID)
        builder.addCommand(issueCommand)
        return builder
    }

    private fun verifyAndSign(transaction: TransactionBuilder): SignedTransaction {
        transaction.verify(serviceHub)
        return serviceHub.signInitialTransaction(transaction)
    }

    @Suspendable
    private fun recordTransaction(transaction: SignedTransaction): SignedTransaction =
            subFlow(FinalityFlow(transaction, emptyList()))

}
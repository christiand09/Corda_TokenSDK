package quantum.transactions

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.KYCContract
import com.quantum.states.IssueTokenState
import com.quantum.states.KYCState
import com.quantum.states.TokenBalance
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
class TokenDoneFlow( private val issueId: String,
                     private val platFormId: String) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        return recordTransaction(signedTransaction)
    }

    private fun inputState(): StateAndRef<KYCState>{
        val platFormID = UniqueIdentifier.fromString(platFormId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(platFormID))
        return serviceHub.vaultService.queryBy<KYCState>(queryCriteria).states.single()
    }

    private fun inputIssueState(): StateAndRef<IssueTokenState>{
        val issueID = UniqueIdentifier.fromString(issueId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(issueID))
        return serviceHub.vaultService.queryBy<IssueTokenState>(queryCriteria).states.single()
    }

    private fun outputState(): KYCState {
        val currency = inputIssueState().state.data.issueToken.token.tokenIdentifier
        val amount = inputIssueState().state.data.issueToken.quantity
        val token = FiatCurrency.getInstance(currency)
        val inputState = inputState().state.data

        lateinit var totalBalanceToken : TokenBalance
        lateinit var totalReceivedToken : TokenBalance
        lateinit var totalSentToken : TokenBalance

        when (currency) {
            "USD" -> {
                val balanceUSD = inputState.balance.usdBalance.quantity + amount
                val balanceReceive = inputState.totalReceived.usdBalance.quantity + amount
                val tokenBalance = Amount(balanceUSD,token)
                val receiveBalance = Amount(balanceReceive,token)
                totalBalanceToken = TokenBalance(usdBalance = tokenBalance,phpBalance = inputState.balance.phpBalance)
                totalReceivedToken = TokenBalance(usdBalance = receiveBalance, phpBalance = inputState.totalReceived.phpBalance)
                totalSentToken = TokenBalance(usdBalance = inputState.totalSent.usdBalance, phpBalance = inputState.totalSent.phpBalance)
            }
            "PHP" -> {
                val balancePHP = inputState.balance.usdBalance.quantity + amount
                val balanceReceive = inputState.totalReceived.usdBalance.quantity + amount
                val tokenBalance = Amount(balancePHP,token)
                val receiveBalance = Amount(balanceReceive,token)
                totalBalanceToken = TokenBalance(usdBalance = inputState.balance.usdBalance,phpBalance = tokenBalance)
                totalReceivedToken = TokenBalance(usdBalance = inputState.totalReceived.usdBalance, phpBalance = receiveBalance)
                totalSentToken = TokenBalance(usdBalance = inputState.totalSent.usdBalance, phpBalance = inputState.totalSent.phpBalance)
            }
            else -> throw IllegalArgumentException("Uncorrected currency")
        }

        return inputState.transfer(totalBalanceToken,totalReceivedToken,totalSentToken)
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(KYCContract.Commands.Register(), ourIdentity.owningKey)
        val builder = TransactionBuilder(notary)
        builder.addInputState(inputState())
        builder.addOutputState(outputState(), KYCContract.KYC_CONTRACT_ID)
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
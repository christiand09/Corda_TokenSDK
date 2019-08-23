package quantum.userTransaction

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.UserContract
import com.quantum.states.UserState
import com.r3.corda.lib.tokens.money.FiatCurrency
import net.corda.core.contracts.Amount
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class UserRegisterFlow( private val firstName: String,
                        private val lastName: String) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        return recordTransaction(signedTransaction)
    }

    private fun outputState(): UserState {
        val usdToken = FiatCurrency.getInstance("USD")
        val phpToken = FiatCurrency.getInstance("PHP")
        val registerValue : Long = 1000
        val noneValue : Long = 0
        val tokens = listOf(Amount(registerValue,usdToken),Amount(registerValue,phpToken))
        val noneToken = listOf(Amount(noneValue,usdToken),Amount(noneValue,phpToken))
        return UserState(
                ourIdentity,
                firstName,
                lastName,
                false,
                tokens,
                noneToken,
                noneToken,
                UniqueIdentifier()
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(UserContract.Commands.Register(), ourIdentity.owningKey)
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), UserContract.USER_CONTRACT_ID)
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
package quantum.transactions

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.UserIssueContract
import com.quantum.states.UserIssueState
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
class DoneUserIssueFlow(private val amount: Long,
                    private val currency: String,
                    private val userId: String) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        return recordTransaction(signedTransaction)
    }

    private fun outputState(): UserIssueState {
        val token = FiatCurrency.getInstance(currency)
        val userID = UniqueIdentifier.fromString(userId)
        return UserIssueState(
                ourIdentity,
                Amount(amount,token),
                userID,
                UniqueIdentifier()
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(UserIssueContract.Commands.Issue(), ourIdentity.owningKey)
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), UserIssueContract.USER_ISSUE_CONTRACT_ID)
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
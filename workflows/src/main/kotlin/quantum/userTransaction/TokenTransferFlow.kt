package quantum.userTransaction

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.IssueTokenContract
import com.quantum.states.IssueTokenState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.serialization.CordaSerializable
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.unwrap

@InitiatingFlow
@StartableByRPC
class TokenTransferFlow(private val issueId: String) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val issuerParty = serviceHub.identityService.partiesFromName("PartyA", false).single()
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        val sessions = initiateFlow(issuerParty)
        sessions.send(UpdatePlatform(issueId, issueState().state.data.owner.toString()))
        val transactionSignedByAllParties = collectSignature(signedTransaction, listOf(sessions))
        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
//        val sessions = (outputState().participants - ourIdentity).map { initiateFlow(it) }.toSet().toList()
//        val transactionSignedByAllParties = collectSignature(signedTransaction, sessions)
//        return recordTransaction(transactionSignedByAllParties,sessions)
    }

    private fun issueState(): StateAndRef<IssueTokenState> {
        val issueID = UniqueIdentifier.fromString(issueId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(issueID))
        return serviceHub.vaultService.queryBy<IssueTokenState>(queryCriteria).states.single()
    }

    private fun outputState(): IssueTokenState {
        val input = issueState().state.data
        return input.transfer()
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(IssueTokenContract.Commands.Issue(), outputState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary)
        builder.addInputState(issueState())///productState
        builder.addOutputState(outputState(), IssueTokenContract.ISSUE_TOKEN_CONTRACT_ID)//subproduct1.1 /
        builder.addCommand(issueCommand)
        return builder
    }

    private fun verifyAndSign(transaction: TransactionBuilder): SignedTransaction {
        transaction.verify(serviceHub)
        return serviceHub.signInitialTransaction(transaction)
    }

    @Suspendable
    private fun collectSignature(
            transaction: SignedTransaction,
            sessions: List<FlowSession>
    ): SignedTransaction = subFlow(CollectSignaturesFlow(transaction, sessions))

    @Suspendable
    private fun recordTransaction(transaction: SignedTransaction, sessions: List<FlowSession>): SignedTransaction =
            subFlow(FinalityFlow(transaction, sessions))

}

@InitiatedBy(TokenTransferFlow::class)
class TokenTransferFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val issueId = flowSession.receive<UpdatePlatform>().unwrap { it }
        val signTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        }
        val signedTransaction = subFlow(signTransactionFlow)
        subFlow(TokenFinishFlow(issueId = issueId.issueId,platFormId = issueId.platFormId))
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = signedTransaction.id))
    }
}

@CordaSerializable
data class UpdatePlatform(
        val issueId: String,
        val platFormId: String
)
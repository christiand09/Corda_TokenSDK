package quantum.userTransaction

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.IssueTokenContract
import com.quantum.contracts.IssueTokenContract.Companion.ISSUE_TOKEN_CONTRACT_ID
import com.quantum.states.IssueTokenState
import com.quantum.states.UserIssueState
import net.corda.core.contracts.*
import net.corda.core.flows.*
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TokenIssueFlow(private val userIssueId: String,
                     private val platformId: String ) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val issuerParty = serviceHub.identityService.partiesFromName("PartyB", false).single()
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        val sessions = initiateFlow(issuerParty)
        val transactionSignedByAllParties = collectSignature(signedTransaction, listOf(sessions))
        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
    }

    private fun inputIssueState(): StateAndRef<UserIssueState> {
        val issueID = UniqueIdentifier.fromString(userIssueId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(issueID))
        return serviceHub.vaultService.queryBy<UserIssueState>(queryCriteria).states.single()
    }

    private fun outputState(): IssueTokenState {
        val issuerParty = serviceHub.identityService.partiesFromName("PartyB", false).single()
        val userID = UniqueIdentifier.fromString(platformId)
        return IssueTokenState(
                ourIdentity,
                issuerParty,
                Amount(inputIssueState().state.data.issueToken.quantity,inputIssueState().state.data.issueToken.token),
                userID,
                false,
                UniqueIdentifier()
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(IssueTokenContract.Commands.Issue(), outputState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), ISSUE_TOKEN_CONTRACT_ID)
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

@InitiatedBy(TokenIssueFlow::class)
class TokenIssueFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        }
        val signedTransaction = subFlow(signTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = signedTransaction.id))
    }
}
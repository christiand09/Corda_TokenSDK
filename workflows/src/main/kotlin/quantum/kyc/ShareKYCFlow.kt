package quantum.kyc

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.KYCContract
import com.quantum.contracts.KYCContract.Companion.KYC_CONTRACT_ID
import com.quantum.states.KYCState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class ShareKYCFlow( private val userId: String) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val otherParty = serviceHub.identityService.partiesFromName("PartyB",false).single()
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        val sessions = initiateFlow(otherParty)
        val transactionSignedByAllParties = collectSignature(signedTransaction, listOf(sessions))
        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
    }

    private fun outputState(): KYCState {
        val userID = UniqueIdentifier.fromString(userId)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(userID))
        val userState = serviceHub.vaultService.queryBy<KYCState>(queryCriteria).states.single()
        val input = userState.state.data
        return input.shareUser()
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val issueCommand = Command(KYCContract.Commands.Share(), outputState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), KYC_CONTRACT_ID)
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

@InitiatedBy(ShareKYCFlow::class)
class ShareKYCFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {

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
//package quantum.wallet
//
//import co.paralleluniverse.fibers.Suspendable
//import com.quantum.states.WalletState
//import net.corda.core.contracts.*
//import net.corda.core.flows.*
//import net.corda.core.identity.Party
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//
//@InitiatingFlow
//@StartableByRPC
//class TransferTokenFlow(private val otherParty: Party,
//                         private val amount: Long,
//                         private val walletId: String) : FlowLogic<SignedTransaction>() {
//
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val transaction = transaction()
//        val signedTransaction = verifyAndSign(transaction)
//        val sessions = initiateFlow(otherParty)
//        val transactionSignedByAllParties = collectSignature(signedTransaction, listOf(sessions))
//        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
//    }
//
//    private fun inputStateAndRef(): StateAndRef<WalletState> {
//        val walletID = UniqueIdentifier.fromString(walletId)
//        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(walletID))
//        return serviceHub.vaultService.queryBy<WalletState>(queryCriteria).states.single()
//    }
//
//    private fun outputState(): WalletState {
//        val input = inputStateAndRef().state.data
//        val totalAmount = input.balance.quantity - amount
//        return input.transfer(Amount(totalAmount,input.balance.token))
//    }
//
//
//    private fun transaction(): TransactionBuilder {
//        val notary = serviceHub.networkMapCache.notaryIdentities.first()
//        val issueCommand = Command(WalletContract.Commands.Register(), outputState().participants.map { it.owningKey })
//        val builder = TransactionBuilder(notary)
//        builder.addInputState(inputStateAndRef())
//        builder.addOutputState(outputState(), WalletContract.WALLET_CONTRACT_ID)
//        builder.addCommand(issueCommand)
//        return builder
//    }
//
//    private fun verifyAndSign(transaction: TransactionBuilder): SignedTransaction {
//        transaction.verify(serviceHub)
//        return serviceHub.signInitialTransaction(transaction)
//    }
//
//    @Suspendable
//    private fun collectSignature(
//            transaction: SignedTransaction,
//            sessions: List<FlowSession>
//    ): SignedTransaction = subFlow(CollectSignaturesFlow(transaction, sessions))
//
//    @Suspendable
//    private fun recordTransaction(transaction: SignedTransaction, sessions: List<FlowSession>): SignedTransaction =
//            subFlow(FinalityFlow(transaction, sessions))
//
//}
//
//@InitiatedBy(TransferTokenFlow::class)
//class TransferTokenFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
//
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val signTransactionFlow = object : SignTransactionFlow(flowSession) {
//            override fun checkTransaction(stx: SignedTransaction) = requireThat {
//            }
//        }
//        val signedTransaction = subFlow(signTransactionFlow)
//        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = signedTransaction.id))
//    }
//}
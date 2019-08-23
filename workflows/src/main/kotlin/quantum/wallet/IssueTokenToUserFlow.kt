//package quantum.wallet
//
//import co.paralleluniverse.fibers.Suspendable
//import com.quantum.states.KYCState
//import com.quantum.states.WalletState
//import com.r3.corda.lib.tokens.money.FiatCurrency
//import net.corda.core.contracts.Amount
//import net.corda.core.contracts.Command
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.contracts.requireThat
//import net.corda.core.flows.*
//import net.corda.core.identity.Party
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//
//@InitiatingFlow
//@StartableByRPC
//class IssueTokenToUserFlow(private val otherParty: Party,
//                         private val amount: Long,
//                         private val currency: String,
//                         private val userId: String) : FlowLogic<SignedTransaction>() {
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
//    private fun outputState(): KYCState {
//        val token = FiatCurrency.getInstance(currency)
//        val userID = UniqueIdentifier.fromString(userId)
//        val zeroAmount: Long = 0
//        return KYCState(
//                ourIdentity,
//                otherParty,
//                Amount(amount, token),
//                userID,
//                Amount(zeroAmount,token),
//                Amount(zeroAmount, token),
//                UniqueIdentifier()
//        )
//    }
//
//
//    private fun transaction(): TransactionBuilder {
//        val notary = serviceHub.networkMapCache.notaryIdentities.first()
//        val issueCommand = Command(WalletContract.Commands.Register(), outputState().participants.map { it.owningKey })
//        val builder = TransactionBuilder(notary)
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
//@InitiatedBy(WalletRegisterFlow::class)
//class WalletRegisterFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
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

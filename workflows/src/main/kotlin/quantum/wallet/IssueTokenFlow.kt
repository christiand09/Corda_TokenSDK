//package quantum.wallet
//
//import co.paralleluniverse.fibers.Suspendable
//import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
//import com.r3.corda.lib.tokens.money.FiatCurrency
//import com.quantum.contracts.IssueTokenContract
//import com.quantum.contracts.IssueTokenContract.Companion.ISSUE_TOKEN_CONTRACT_ID
//import com.quantum.states.FungibleIssueTokenState
//import net.corda.core.contracts.Amount
//import net.corda.core.contracts.Command
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.contracts.requireThat
//import net.corda.core.flows.*
//import net.corda.core.identity.Party
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//import java.math.BigDecimal
//
//@InitiatingFlow
//@StartableByRPC
//class IssueTokenFlow(private val otherParty: Party,
//                     private val amount: Long,
//                     private val currency: String,
//                     private val nameIssuer: String,
//                     private val messageForIssue: String) : FlowLogic<SignedTransaction>() {
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val transaction = transaction()
//        val signedTransaction = verifyAndSign(transaction)
////        val sessions = (outputState().participants - ourIdentity).map { initiateFlow(it) }.toSet().toList()
//        val sessions = initiateFlow(otherParty)
//        val transactionSignedByAllParties = collectSignature(signedTransaction, listOf(sessions))
//        return recordTransaction(transactionSignedByAllParties, listOf(sessions))
//    }
//
//    private fun outputState(): FungibleIssueTokenState {
//        val token = FiatCurrency.getInstance(currency)
//        return FungibleIssueTokenState(
//                ourIdentity,
//                otherParty,
//                nameIssuer,
//                messageForIssue,
//                UniqueIdentifier(),
//                amount = Amount(amount, BigDecimal(amount),IssuedTokenType(ourIdentity,token)),
//                holder = ourIdentity
//        )
//    }
//
//
//
//    //Creating the Transaction
//    private fun transaction(): TransactionBuilder {
//        val notary = serviceHub.networkMapCache.notaryIdentities.first()
//        val issueCommand = Command(IssueTokenContract.Commands.Register(), outputState().participants.map { it.owningKey })
//        val builder = TransactionBuilder(notary)
//        builder.addOutputState(outputState(), ISSUE_TOKEN_CONTRACT_ID)
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
//    private fun recordTransaction(transaction: SignedTransaction,sessions: List<FlowSession>): SignedTransaction =
//            subFlow(FinalityFlow(transaction, sessions))
//
//}
//
//@InitiatedBy(IssueTokenFlow::class)
//class IssueTokenFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {
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
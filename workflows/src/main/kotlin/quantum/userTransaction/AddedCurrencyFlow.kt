//package quantum.userTransaction
//
//import co.paralleluniverse.fibers.Suspendable
//import com.quantum.contracts.UserContract
//import com.quantum.states.UserIssueState
//import com.quantum.states.UserState
//import com.r3.corda.lib.tokens.contracts.types.TokenType
//import com.r3.corda.lib.tokens.money.FiatCurrency
//import net.corda.core.contracts.Amount
//import net.corda.core.contracts.Command
//import net.corda.core.contracts.StateAndRef
//import net.corda.core.contracts.UniqueIdentifier
//import net.corda.core.flows.FinalityFlow
//import net.corda.core.flows.FlowLogic
//import net.corda.core.flows.InitiatingFlow
//import net.corda.core.flows.StartableByRPC
//import net.corda.core.node.services.queryBy
//import net.corda.core.node.services.vault.QueryCriteria
//import net.corda.core.transactions.SignedTransaction
//import net.corda.core.transactions.TransactionBuilder
//
//@InitiatingFlow
//@StartableByRPC
//class AddedCurrencyFlow(private val userId: String,
//                        private val platformId: String) : FlowLogic<SignedTransaction>() {
//    @Suspendable
//    override fun call(): SignedTransaction {
//        val transaction = transaction()
//        val signedTransaction = verifyAndSign(transaction)
//        return recordTransaction(signedTransaction)
//    }
//
//    private fun inputState(): StateAndRef<UserState> {
//        val platFormID = UniqueIdentifier.fromString(platformId)
//        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(platFormID))
//        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
//    }
//
//    private fun userInputState(): StateAndRef<UserState> {
//        val userID = UniqueIdentifier.fromString(userId)
//        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(userID))
//        return serviceHub.vaultService.queryBy<UserState>(queryCriteria).states.single()
//    }
//
//    private fun outputState(): UserState{
//        return UserState()
//    }
//
//    private fun transaction(): TransactionBuilder {
//        val notary = serviceHub.networkMapCache.notaryIdentities.first()
//        val issueCommand = Command(UserContract.Commands.Register(), ourIdentity.owningKey)
//        val builder = TransactionBuilder(notary)
//        builder.addInputState(userInputState())
//        builder.addInputState(inputState())
//        builder.addOutputState(outputState(), UserContract.USER_CONTRACT_ID)
//        builder.addOutputState(userOutputState(), UserContract.USER_CONTRACT_ID)
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
//    private fun recordTransaction(transaction: SignedTransaction): SignedTransaction =
//            subFlow(FinalityFlow(transaction, emptyList()))
//
//}
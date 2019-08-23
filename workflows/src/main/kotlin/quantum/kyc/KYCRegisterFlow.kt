package quantum.kyc

import co.paralleluniverse.fibers.Suspendable
import com.quantum.contracts.KYCContract
import com.quantum.contracts.KYCContract.Companion.KYC_CONTRACT_ID
import com.quantum.states.KYCState
import com.quantum.states.TokenBalance
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
class KYCRegister(private val firstName: String,
                  private val lastName: String) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val transaction = transaction()
        val signedTransaction = verifyAndSign(transaction)
        return recordTransaction(signedTransaction)
    }

    private fun outputState(): KYCState {
        val usdToken = FiatCurrency.getInstance("USD")
        val phpToken = FiatCurrency.getInstance("PHP")
        val registerValue : Long = 1000
        val noneValue : Long = 0
        val tokens = TokenBalance(usdBalance = Amount(registerValue,usdToken),phpBalance = Amount(registerValue,phpToken))
        val noneToken = TokenBalance(usdBalance = Amount(noneValue,usdToken),phpBalance = Amount(noneValue,phpToken))
        return KYCState(
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
        val issueCommand = Command(KYCContract.Commands.Register(), ourIdentity.owningKey)
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
    private fun recordTransaction(transaction: SignedTransaction): SignedTransaction =
            subFlow(FinalityFlow(transaction, emptyList()))

}
package com.quantum.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class KYCContract : Contract {

    companion object {
        @JvmStatic
        val KYC_CONTRACT_ID = "com.quantum.contracts.KYCContract"
    }

    interface Commands : CommandData {
        class Register : TypeOnlyCommandData(), Commands
        class Share : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Register -> requireThat {
            }
            is Commands.Share -> requireThat {
            }
        }
    }
}
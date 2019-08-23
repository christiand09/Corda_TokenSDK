package com.quantum.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class UserIssueContract : Contract {

    companion object {
        @JvmStatic
        val USER_ISSUE_CONTRACT_ID = "com.quantum.contracts.UserIssueContract"
    }

    interface Commands : CommandData {
        class Issue : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
            }
        }
    }
}
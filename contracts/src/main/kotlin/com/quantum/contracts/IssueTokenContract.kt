package com.quantum.contracts

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class IssueTokenContract : Contract {

    companion object {
        @JvmStatic
        val ISSUE_TOKEN_CONTRACT_ID = "com.quantum.contracts.IssueTokenContract"
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
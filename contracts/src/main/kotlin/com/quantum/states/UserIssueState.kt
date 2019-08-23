package com.quantum.states

import com.quantum.contracts.UserIssueContract
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party


@BelongsToContract(UserIssueContract::class)
data class UserIssueState(
        val ourParty: Party,
        val issueToken: Amount<TokenType>,
        val owner: UniqueIdentifier,
        override val linearId: UniqueIdentifier = UniqueIdentifier(),
        override val participants: List<Party> = listOf(ourParty)
) : LinearState {

//    fun transfer(amount: Amount<TokenType>): WalletState = copy(balance = amount)

}
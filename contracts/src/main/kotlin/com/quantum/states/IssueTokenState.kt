package com.quantum.states

import com.quantum.contracts.IssueTokenContract
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(IssueTokenContract::class)
data class IssueTokenState(
        val ourParty: Party,
        val otherParty: Party,
        val issueToken: Amount<TokenType>,
        val owner: UniqueIdentifier,
        val done: Boolean,
        override val linearId: UniqueIdentifier = UniqueIdentifier(),
        override val participants: List<Party> = listOf(ourParty,otherParty)
) : LinearState {

    fun transfer(): IssueTokenState = copy(done = true)

}
package com.quantum.states

import com.quantum.contracts.KYCContract
import com.quantum.contracts.UserContract
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(UserContract::class)
data class UserState(
        val ourParty: Party,
        val otherParty: Party,
        val firstName: String,
        val lastName: String,
        val shared: Boolean,
        val balance: List<Amount<TokenType>>,
        val totalSent: List<Amount<TokenType>>,
        val totalReceived: List<Amount<TokenType>>,
        override val linearId: UniqueIdentifier = UniqueIdentifier(),
        override val participants: List<Party> = listOf(ourParty, otherParty)) : LinearState {


    fun transfer(balance: List<Amount<TokenType>>,receive: List<Amount<TokenType>>,sent: List<Amount<TokenType>>): UserState = copy(balance = balance,totalReceived = receive,totalSent = sent)
    fun addedCurrency(balance: List<Amount<TokenType>>,totalSent: List<Amount<TokenType>>,totalReceived: List<Amount<TokenType>>): UserState = copy(balance = balance,totalReceived = totalReceived,totalSent = totalSent)

    constructor(
            ourParty: Party,
            firstName: String,
            lastName: String,
            shared: Boolean,
            balance: List<Amount<TokenType>>,
            totalSent: List<Amount<TokenType>>,
            totalReceived: List<Amount<TokenType>>,
            linearId: UniqueIdentifier = UniqueIdentifier(),
            participants: List<Party> = listOf(ourParty)
    ) : this(ourParty,ourParty,firstName,lastName,shared,balance,totalSent,totalReceived,linearId,participants)
}
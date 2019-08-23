package com.quantum.states

import com.quantum.contracts.KYCContract
import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@BelongsToContract(KYCContract::class)
data class KYCState(
        val ourParty: Party,
        val otherParty: Party,
        val firstName: String,
        val lastName: String,
        val shared: Boolean,
        val balance: TokenBalance,
        val totalSent: TokenBalance,
        val totalReceived: TokenBalance,
        override val linearId: UniqueIdentifier = UniqueIdentifier(),
        override val participants: List<Party> = listOf(ourParty, otherParty)) : LinearState {

    fun transfer(amount: TokenBalance,receive: TokenBalance,sent: TokenBalance): KYCState = copy(balance = amount,totalReceived = receive,totalSent = sent)
    fun sent(amount: TokenBalance,sent: TokenBalance): KYCState = copy(balance = amount,totalSent = sent)
    fun shareUser(): KYCState = copy(shared = true)
//    fun test(): KYCState = copy()

    constructor(
            ourParty: Party,
            firstName: String,
            lastName: String,
            shared: Boolean,
            balance: TokenBalance,
            totalSent: TokenBalance,
            totalReceived: TokenBalance,
            linearId: UniqueIdentifier = UniqueIdentifier(),
            participants: List<Party> = listOf(ourParty)
    ) : this(ourParty,ourParty,firstName,lastName,shared,balance,totalSent,totalReceived,linearId,participants)
}

@CordaSerializable
data class TokenBalance(
        val usdBalance: Amount<TokenType>,
        val phpBalance: Amount<TokenType>
        )

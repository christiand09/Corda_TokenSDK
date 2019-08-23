//package com.quantum.states
//
//import com.r3.corda.lib.tokens.contracts.states.AbstractToken
//import com.r3.corda.lib.tokens.contracts.types.IssuedTokenType
//import com.r3.corda.lib.tokens.contracts.types.TokenType
//import com.r3.corda.lib.tokens.contracts.utilities.getAttachmentIdForGenericParam
//import com.r3.corda.lib.tokens.contracts.utilities.holderString
//import com.quantum.contracts.IssueTokenContract
//import net.corda.core.contracts.*
//import net.corda.core.crypto.SecureHash
//import net.corda.core.identity.AbstractParty
//import net.corda.core.identity.Party
//import net.corda.core.serialization.CordaSerializable
//
//@BelongsToContract(IssueTokenContract::class)
//data class FungibleIssueTokenState(
//        val ourParty: Party,
//        val otherParty: Party,
//        val name: String,
//        val message: String,
//        override val linearId: UniqueIdentifier = UniqueIdentifier(),
//        override val participants: List<Party> = listOf(ourParty, otherParty),
//        override val amount: Amount<IssuedTokenType>,
//        override val holder: AbstractParty,
//        override val tokenTypeJarHash: SecureHash? = amount.token.tokenType.getAttachmentIdForGenericParam()
//) : LinearState, AbstractToken, FungibleState<IssuedTokenType> {
//
//    constructor(
//            ourParty: Party,
//            name: String,
//            message: String,
//            linearId: UniqueIdentifier = UniqueIdentifier(),
//            participants: List<Party> = listOf(ourParty),
//            amount: Amount<IssuedTokenType>,
//            holder: AbstractParty,
//            tokenTypeJarHash: SecureHash? = amount.token.tokenType.getAttachmentIdForGenericParam()
//    ): this(ourParty,ourParty,name,message,linearId,participants,amount,holder,tokenTypeJarHash)
//
//        override val tokenType: TokenType get() = amount.token.tokenType
//
//        override val issuedTokenType: IssuedTokenType get() = amount.token
//
//        override val issuer: Party get() = amount.token.issuer
//
//        override fun toString(): String = "$amount held by $holderString"
//
//        override fun withNewHolder(newHolder: AbstractParty): AbstractToken {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        fun moveToken(amountMove: Amount<IssuedTokenType>): FungibleIssueTokenState = copy(amount = amountMove)
//
//}
//
//@CordaSerializable
//data class PartyAndAmount<T : TokenType>(val party: AbstractParty, val amount: Amount<T>)
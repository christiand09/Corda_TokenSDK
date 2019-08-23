package com.quantum.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.quantum.states.TokenBalance
import net.corda.core.identity.Party

data class KYCModel(
        val ourParty: Party,
        val otherParty: Party,
        val firstName: String,
        val lastName: String,
        val shared: Boolean,
        val balance: TokenBalance,
        val totalSent: TokenBalance,
        val totalReceived: TokenBalance,
        val linearId: String
)

data class KYCRegisterModel @JsonCreator constructor(
        val firstName: String,
        val lastName: String
)

data class ShareKYCModel @JsonCreator constructor(
        val userId: String
)

data class TokenDoneModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
)

data class UserTransferModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
)

data class TokenFinishModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
)
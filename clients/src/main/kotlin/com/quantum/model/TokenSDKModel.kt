package com.quantum.model

import com.fasterxml.jackson.annotation.JsonCreator

data class KYCModel(
        val ourParty: String,
        val otherParty: String,
        val firstName: String,
        val lastName: String,
        val shared: String,
        val balance: String,
        val totalSent: String,
        val totalReceived: String,
        val linearId: String
)

data class IssueTokenModel(
        val ourParty: String,
        val otherParty: String,
        val issueToken: String,
        val owner: String,
        val done: String,
        val linearId: String
)

data class UserIssueModel(
        val ourParty: String,
        val issueToken: String,
        val owner: String,
        val linearId: String
)

data class UserModel(
        val ourParty: String,
        val otherParty: String,
        val firstName: String,
        val lastName: String,
        val shared: String,
        val balance: String,
        val totalSent: String,
        val totalReceived: String,
        val linearId: String
)

data class TestModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
)

data class  TokenIssueModel @JsonCreator constructor(
        val userIssueId: String,
        val platformId: String
)

data class  TokenTransferModel @JsonCreator constructor(
        val issueId: String
)

data class DoneUserIssueModel @JsonCreator constructor(
        val amount: Long,
        val currency: String,
        val userId: String
)

data class UserIssueFlowModel @JsonCreator constructor(
        val amount: Long,
        val currency: String,
        val userId: String
)

data class UserTransferTokenModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
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

data class UserRegisterModel @JsonCreator constructor(
        val firstName: String,
        val lastName: String
)
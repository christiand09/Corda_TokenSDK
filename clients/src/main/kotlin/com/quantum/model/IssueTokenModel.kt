package com.quantum.model

import com.fasterxml.jackson.annotation.JsonCreator

data class IssueTokenModel(
        val ourParty: String,
        val otherParty: String,
        val issueToken: String,
        val owner: String,
        val done: String,
        val linearId: String
)

data class TestModel @JsonCreator constructor(
        val issueId: String,
        val platFormId: String
)
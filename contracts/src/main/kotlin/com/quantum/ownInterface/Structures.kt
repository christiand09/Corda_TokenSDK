package com.quantum.ownInterface

import com.r3.corda.lib.tokens.contracts.types.TokenType
import net.corda.core.KeepForDJVM
import net.corda.core.contracts.Amount
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier

@KeepForDJVM
interface AmountQuantum : ContractState {
    val totalSent: Amount<TokenType>
    val totalReceived: Amount<TokenType>
}
package com.quantum.controller

import com.quantum.model.*
import com.quantum.states.KYCState
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import quantum.kyc.KYCRegister
import quantum.kyc.ShareKYCFlow
import quantum.transactions.TokenDoneFlow

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class KYCController(rpc: NodeRPCConnection,
                         private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    /**
     *  AddDocumentFlow
     *  Controller
     */
    @GetMapping(value = ["/get/kyc"], produces = ["application/json"])
    private fun getAllKYC(): ResponseEntity<ResponseModel> {
        val (status, result) = try {
            val userStateRef = proxy.vaultQuery(KYCState::class.java).states
            val userState = userStateRef.map { it.state.data }
            val user = userState.map {
                KYCModel(
                ourParty = it.ourParty,
                otherParty = it.otherParty,
                firstName = it.firstName,
                lastName = it.lastName,
                shared = it.shared,
                balance = it.balance,
                totalSent = it.totalSent,
                totalReceived = it.totalReceived,
                linearId = it.linearId.toString()
                )
            }
            HttpStatus.CREATED to user
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "get all documents successful", result)
        } else {
            ResponseModel("failed", "get all documents unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }

    /**
     * API KYC Register
     */
    @PostMapping(value = ["/kyc/registerKyc"], produces = ["application/json"])
    private fun registerKYC(@RequestBody register: KYCRegisterModel): ResponseEntity<ResponseModel> {

        val registerKYC = KYCRegisterModel(
                firstName = register.firstName,
                lastName = register.lastName
                )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    KYCRegister::class.java,
                    registerKYC.firstName,
                    registerKYC.lastName
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to register
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "company registration successful", result)
        } else {
            ResponseModel("failed", "company registration unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }

    /**
     * API KYC Share
     */
    @PostMapping(value = ["/kyc/shareKYC"], produces = ["application/json"])
    private fun shareKYC(@RequestBody share: ShareKYCModel): ResponseEntity<ResponseModel> {

        val shareKYC = ShareKYCModel(
                userId = share.userId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    ShareKYCFlow::class.java,
                    shareKYC.userId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to share
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "company registration successful", result)
        } else {
            ResponseModel("failed", "company registration unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }

    /**
     * API TokenDoneFlow
     */
    @PostMapping(value = ["/kyc/tokenDone"], produces = ["application/json"])
    private fun tokenDone(@RequestBody done: TokenDoneModel): ResponseEntity<ResponseModel> {

        val tokenDone = TokenDoneModel(
                issueId = done.issueId,
                platFormId = done.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenDoneFlow::class.java,
                    tokenDone.issueId,
                    tokenDone.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to done
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "company registration successful", result)
        } else {
            ResponseModel("failed", "company registration unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }

    /**
     * API UserTransferFlow
     */
    @PostMapping(value = ["/kyc/userTransfer"], produces = ["application/json"])
    private fun forTransferUser(@RequestBody userTransfer: UserTransferModel): ResponseEntity<ResponseModel> {

        val transferUser = UserTransferModel(
                issueId = userTransfer.issueId,
                platFormId = userTransfer.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenDoneFlow::class.java,
                    transferUser.issueId,
                    transferUser.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to userTransfer
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "company registration successful", result)
        } else {
            ResponseModel("failed", "company registration unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }

    /**
     * API for TokenFinishFlow
     */
    @PostMapping(value = ["/kyc/tokenFinish"], produces = ["application/json"])
    private fun forTokenFinish(@RequestBody tokenFinish: TokenFinishModel): ResponseEntity<ResponseModel> {

        val token = TokenFinishModel(
                issueId = tokenFinish.issueId,
                platFormId = tokenFinish.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenDoneFlow::class.java,
                    token.issueId,
                    token.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to token
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
            HttpStatus.BAD_REQUEST to String.format("No data - %s", e.message.toString())
        }
        val res: ResponseModel = if (status.value() == 200 || status.value() == 201) {
            ResponseModel("success", "company registration successful", result)
        } else {
            ResponseModel("failed", "company registration unsuccessful", result)
        }
        return ResponseEntity.status(status).body(res)
    }


}
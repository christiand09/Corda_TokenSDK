package com.quantum.controller

import com.quantum.model.*
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import quantum.userTransaction.*

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/userTransaction") // The paths for HTTP requests are relative to this base path.
class UserTransactionController(rpc: NodeRPCConnection,
                          private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    /**
     * API for TokenFinishFlow
     */
    @PostMapping(value = ["/tokenFinish"], produces = ["application/json"])
    private fun forTokenFinish(@RequestBody tokenFinish: TokenFinishModel): ResponseEntity<ResponseModel> {

        val token = TokenFinishModel(
                issueId = tokenFinish.issueId,
                platFormId = tokenFinish.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenFinishFlow::class.java,
                    token.issueId,
                    token.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to token
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
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
     * API for TokenIssueFlow
     */
    @PostMapping(value = ["/tokenIssue"], produces = ["application/json"])
    private fun forTokenIssueFlow(@RequestBody tokenIssue: TokenIssueModel): ResponseEntity<ResponseModel> {

        val issue = TokenIssueModel(
                userIssueId = tokenIssue.userIssueId,
                platformId = tokenIssue.platformId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenIssueFlow::class.java,
                    issue.userIssueId,
                    issue.platformId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to issue
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
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
     * API for TokenTransferFlow
     */
    @PostMapping(value = ["/tokenTransfer"], produces = ["application/json"])
    private fun forTokenTransferFlow(@RequestBody tokenTransfer: TokenTransferModel): ResponseEntity<ResponseModel> {

        val transfer = TokenTransferModel(
                issueId = tokenTransfer.issueId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TokenTransferFlow::class.java,
                    transfer.issueId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to transfer
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
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
     * API for UserRegisterFlow
     */
    @PostMapping(value = ["/userRegister"], produces = ["application/json"])
    private fun forUserRegisterFLow(@RequestBody userRegister: UserRegisterModel): ResponseEntity<ResponseModel> {

        val register = UserRegisterModel(
                firstName = userRegister.firstName,
                lastName = userRegister.lastName
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    UserRegisterFlow::class.java,
                    register.firstName,
                    register.lastName
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to register
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
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
     * API for UserTransferTokenFlow
     */
    @PostMapping(value = ["/userIssue/userTranferToken"], produces = ["application/json"])
    private fun forUserTransferTokenFlow(@RequestBody userTransfer: UserTransferTokenModel): ResponseEntity<ResponseModel> {

        val transfer = UserTransferTokenModel(
                issueId = userTransfer.issueId,
                platFormId = userTransfer.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    UserTransferTokenFlow::class.java,
                    transfer.issueId,
                    transfer.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to transfer
        } catch (e: Exception) {
            logger.error(e.printStackTrace().toString())
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
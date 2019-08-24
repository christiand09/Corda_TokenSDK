package com.quantum.controller

import com.quantum.model.*
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import quantum.transactions.*

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/transaction") // The paths for HTTP requests are relative to this base path.
class TransactionController(rpc: NodeRPCConnection,
                     private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    /**
     * API for DoneUserIssueFlow
     */
    @PostMapping(value = ["/doneUserIssue"], produces = ["application/json"])
    private fun forDoneUserIssueFlow(@RequestBody userIssue: DoneUserIssueModel): ResponseEntity<ResponseModel> {

        val done = DoneUserIssueModel(
                amount = userIssue.amount,
                currency = userIssue.currency,
                userId = userIssue.userId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    DoneUserIssueFlow::class.java,
                    done.amount,
                    done.currency,
                    done.userId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to done
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
     * API for
     * TestFlow
     */
    @PostMapping(value = ["/test"], produces = ["application/json"])
    private fun forTestFlow(@RequestBody test: TestModel): ResponseEntity<ResponseModel> {

        val testFlow = TestModel(
                issueId = test.issueId,
                platFormId = test.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    TestFlow::class.java,
                    testFlow.issueId,
                    testFlow.platFormId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to testFlow
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
     * API TokenDoneFlow
     */
    @PostMapping(value = ["/tokenDone"], produces = ["application/json"])
    private fun forTokenDoneFlow(@RequestBody done: TokenDoneModel): ResponseEntity<ResponseModel> {

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
     * API for UserIssueFLow
     */
    @PostMapping(value = ["/UserIssueFlow"], produces = ["application/json"])
    private fun forUserIssueFlow(@RequestBody userIssue: UserIssueFlowModel): ResponseEntity<ResponseModel> {

        val done = UserIssueFlowModel(
                amount = userIssue.amount,
                currency = userIssue.currency,
                userId = userIssue.userId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    UserIssueFlow::class.java,
                    done.amount,
                    done.currency,
                    done.userId
            )
            flowHandlerCompletion.flowHandlerCompletion(flowReturn)
            HttpStatus.CREATED to done
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
     * API UserTransferFlow
     */
    @PostMapping(value = ["/userTransfer"], produces = ["application/json"])
    private fun forUserTransferUser(@RequestBody userTransfer: UserTransferModel): ResponseEntity<ResponseModel> {

        val transferUser = UserTransferModel(
                issueId = userTransfer.issueId,
                platFormId = userTransfer.platFormId
        )

        val (status, result) = try {
            val flowReturn = proxy.startFlowDynamic(
                    UserTransferFlow::class.java,
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

}
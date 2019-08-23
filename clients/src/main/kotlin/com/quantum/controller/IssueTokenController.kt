package com.quantum.controller

import com.quantum.model.IssueTokenModel
import com.quantum.model.ResponseModel
import com.quantum.model.TestModel
import com.quantum.states.IssueTokenState
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import quantum.transactions.TestFlow

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class IssueTokenController(rpc: NodeRPCConnection,
                           private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    /**
     *  Get All
     *  IssueToken
     */
    @GetMapping(value = ["/get/issueToken"], produces = ["application/json"])
    private fun getAllIssueToken(): ResponseEntity<ResponseModel> {
        val (status, result) = try {
            val userStateRef = proxy.vaultQuery(IssueTokenState::class.java).states
            val userState = userStateRef.map { it.state.data }
            val user = userState.map {
                IssueTokenModel(
                        ourParty = it.ourParty.toString(),
                        otherParty = it.otherParty.toString(),
                        issueToken = it.issueToken.toString(),
                        owner = it.owner.toString(),
                        done = it.done.toString(),
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
     * API for TestFlow
     */
    @PostMapping(value = ["/issueToken/test"], produces = ["application/json"])
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
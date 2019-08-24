package com.quantum.controller

import com.quantum.model.*
import com.quantum.states.IssueTokenState
import com.quantum.states.KYCState
import com.quantum.states.UserIssueState
import com.quantum.states.UserState
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/get") // The paths for HTTP requests are relative to this base path.
class GetController(rpc: NodeRPCConnection,
                           private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    /**
     *  Get All
     *  IssueTokenState
     */
    @GetMapping(value = ["/issueToken"], produces = ["application/json"])
    private fun getAllIssueTokenState(): ResponseEntity<ResponseModel> {
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
     *  Get All
     *  KYCState
     */
    @GetMapping(value = ["/kycState"], produces = ["application/json"])
    private fun getAllKYC(): ResponseEntity<ResponseModel> {
        val (status, result) = try {
            val userStateRef = proxy.vaultQuery(KYCState::class.java).states
            val userState = userStateRef.map { it.state.data }
            val user = userState.map {
                KYCModel(
                        ourParty = it.ourParty.toString(),
                        otherParty = it.otherParty.toString(),
                        firstName = it.firstName,
                        lastName = it.lastName,
                        shared = it.shared.toString(),
                        balance = it.balance.toString(),
                        totalSent = it.totalSent.toString(),
                        totalReceived = it.totalReceived.toString(),
                        linearId = it.linearId.toString()
                )
            }
            HttpStatus.CREATED to user
        } catch (e: Exception) {
            KYCController.logger.error(e.printStackTrace().toString())
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
     *  Get All
     *  UserState
     */
    @GetMapping(value = ["/userState"], produces = ["application/json"])
    private fun getAllUserState(): ResponseEntity<ResponseModel> {
        val (status, result) = try {
            val userStateRef = proxy.vaultQuery(UserState::class.java).states
            val userState = userStateRef.map { it.state.data }
            val user = userState.map {
                UserModel(
                        ourParty = it.ourParty.toString(),
                        otherParty = it.otherParty.toString(),
                        firstName = it.firstName,
                        lastName = it.lastName,
                        shared = it.shared.toString(),
                        balance = it.balance.toString(),
                        totalSent = it.totalSent.toString(),
                        totalReceived = it.totalReceived.toString(),
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
     *  GetAll
     *  IssueIssueState
     */
    @GetMapping(value = ["/get/userIssue"], produces = ["application/json"])
    private fun getAllIssueState(): ResponseEntity<ResponseModel> {
        val (status, result) = try {
            val userStateRef = proxy.vaultQuery(UserIssueState::class.java).states
            val userState = userStateRef.map { it.state.data }
            val user = userState.map {
                UserIssueModel(
                        ourParty = it.ourParty.toString(),
                        issueToken = it.issueToken.toString(),
                        owner = it.owner.toString(),
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
}
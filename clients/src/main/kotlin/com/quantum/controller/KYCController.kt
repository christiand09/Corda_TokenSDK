package com.quantum.controller

import com.quantum.model.KYCRegisterModel
import com.quantum.model.ResponseModel
import com.quantum.model.ShareKYCModel
import com.quantum.utilities.FlowHandlerCompletion
import com.quantum.webserver.NodeRPCConnection
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import quantum.kyc.KYCRegister
import quantum.kyc.ShareKYCFlow

private const val CONTROLLER_NAME = "config.controller"

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/kyc") // The paths for HTTP requests are relative to this base path.
class KYCController(rpc: NodeRPCConnection,
                         private val flowHandlerCompletion: FlowHandlerCompletion) {

    companion object {
        val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy



    /**
     * API KYC Register
     */
    @PostMapping(value = ["/registerKyc"], produces = ["application/json"])
    private fun forRegisterKYC(@RequestBody register: KYCRegisterModel): ResponseEntity<ResponseModel> {

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
     * API KYC Share
     */
    @PostMapping(value = ["/shareKYC"], produces = ["application/json"])
    private fun forShareKYC(@RequestBody share: ShareKYCModel): ResponseEntity<ResponseModel> {

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
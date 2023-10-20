package com.sdk.growthbook.tests

import com.sdk.growthbook.GBSDKBuilder
import com.sdk.growthbook.Utils.DefaultCrypto
import com.sdk.growthbook.Utils.encryptToFeaturesDataModel
import com.sdk.growthbook.model.GBExperiment
import com.sdk.growthbook.model.GBExperimentResult
import com.soywiz.krypto.encoding.Base64
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.serialization.json.JsonArray
import kotlin.test.Test
import kotlin.test.assertEquals

class GBEncryptedFeatures {

    @Test
    fun testEncryptDecrypt() {
        val testCases = GBTestHelper.getDecryptData()
        testCases.forEach { jsonElement ->
            val test: JsonArray = jsonElement as JsonArray

            val ivString = test[1].toString()
            val keyString = test[2].toString()
            val stringForEncrypt = test[3].toString()

            val decodedIv = Base64.encode(ivString.toByteArray()).toByteArray()
            val decodedKey = Base64.encode(keyString.toByteArray()).toByteArray()

            val defaultCrypto = DefaultCrypto()

            val encryptedValue = defaultCrypto.encrypt(
                stringForEncrypt.toByteArray(),
                decodedKey,
                decodedIv
            )
            val decryptedValue = defaultCrypto.decrypt(
                encryptedValue,
                decodedKey,
                decodedIv
            )
            println(stringForEncrypt)
            println(String(decryptedValue))
            assertEquals(stringForEncrypt, String(decryptedValue))
        }
    }

    val testApiKey = "4r23r324f23"
    val testHostURL = "https://host.com"
    val testAttributes: HashMap<String, Any> = HashMap()

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun testEncrypt() {

        val sdkInstance = GBSDKBuilder(
            testApiKey,
            testHostURL,
            attributes = testAttributes,
            //TODO
            encryptionKey = "",
            trackingCallback = { gbExperiment: GBExperiment, gbExperimentResult: GBExperimentResult ->

            }).initialize()

        val keyString = "Ns04T5n9+59rl2x3SlNHtQ=="
        val encryptedFeatures =
            "vMSg2Bj/IurObDsWVmvkUg==.L6qtQkIzKDoE2Dix6IAKDcVel8PHUnzJ7JjmLjFZFQDqidRIoCxKmvxvUj2kTuHFTQ3/NJ3D6XhxhXXv2+dsXpw5woQf0eAgqrcxHrbtFORs18tRXRZza7zqgzwvcznx"
        val expectedResult =
            "{\"testfeature1\":{\"defaultValue\":true,\"rules\":[{\"condition\":{\"id\":\"1234\"},\"force\":false}]}}"

        sdkInstance.setEncryptedFeatures(encryptedFeatures, keyString, null)

        val features = encryptToFeaturesDataModel(expectedResult)!!
        println(features)
        assertEquals(
            features["testfeature1"]?.rules?.get(0)?.condition,
            sdkInstance.getGBContext().features["testfeature1"]?.rules?.get(0)?.condition
        )
        assertEquals(
            features["testfeature1"]?.rules?.get(0)?.force,
            sdkInstance.getGBContext().features["testfeature1"]?.rules?.get(0)?.force
        )
    }

    @Test
    fun testDecrypt() {
    }
}
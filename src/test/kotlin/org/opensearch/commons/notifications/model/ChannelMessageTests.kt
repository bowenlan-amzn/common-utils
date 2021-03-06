/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package org.opensearch.commons.notifications.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.opensearch.commons.utils.createObjectFromJsonString
import org.opensearch.commons.utils.getJsonString
import org.opensearch.commons.utils.recreateObject

internal class ChannelMessageTests {

    @Test
    fun `ChannelMessage Object serialize and deserialize using transport should be equal`() {
        val attachment = Attachment(
            "fileName",
            "fileEncoding",
            "fileData",
            "fileContentType"
        )
        val channelMessage = ChannelMessage(
            "textDescription",
            "<html>htmlDescription</html>",
            attachment
        )
        val recreatedObject = recreateObject(channelMessage) { ChannelMessage(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Object serialize and deserialize using json should be equal`() {
        val attachment = Attachment(
            "fileName",
            "fileEncoding",
            "fileData",
            "fileContentType"
        )
        val channelMessage = ChannelMessage(
            "textDescription",
            "<html>htmlDescription</html>",
            attachment
        )
        val jsonString = getJsonString(channelMessage)
        val recreatedObject = createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Json parsing should safely ignore extra fields`() {
        val attachment = Attachment(
            "fileName",
            "fileEncoding",
            "fileData",
            "fileContentType"
        )
        val channelMessage = ChannelMessage(
            "textDescription",
            "<html>htmlDescription</html>",
            attachment
        )
        val jsonString = """
        {
            "text_description":"textDescription",
            "html_description":"<html>htmlDescription</html>",
            "attachment":{
                "file_name":"fileName",
                "file_encoding":"fileEncoding",
                "file_data":"fileData",
                "file_content_type":"fileContentType"
            },
            "extra_field_1":["extra", "value"],
            "extra_field_2":{"extra":"value"},
            "extra_field_3":"extra value 3"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Json parsing should safely ignore missing html description`() {
        val attachment = Attachment(
            "fileName",
            "fileEncoding",
            "fileData",
            "fileContentType"
        )
        val channelMessage = ChannelMessage(
            "textDescription",
            null,
            attachment
        )
        val jsonString = """
        {
            "text_description":"textDescription",
            "attachment":{
                "file_name":"fileName",
                "file_encoding":"fileEncoding",
                "file_data":"fileData",
                "file_content_type":"fileContentType"
            }
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Json parsing should safely ignore missing attachment`() {
        val channelMessage = ChannelMessage(
            "textDescription",
            "<html>htmlDescription</html>",
            null
        )
        val jsonString = """
        {
            "text_description":"textDescription",
            "html_description":"<html>htmlDescription</html>"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Json parsing should safely ignore both missing html_description and attachment`() {
        val channelMessage = ChannelMessage(
            "textDescription",
            null,
            null
        )
        val jsonString = """
        {
            "text_description":"textDescription"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        assertEquals(channelMessage, recreatedObject)
    }

    @Test
    fun `ChannelMessage Json parsing should throw exception if text_description is empty`() {
        val jsonString = """
        {
            "text_description":"",
            "html_description":"<html>htmlDescription</html>",
            "attachment":{
                "file_name":"fileName",
                "file_encoding":"fileEncoding",
                "file_data":"fileData",
                "file_content_type":"fileContentType"
            }
        }
        """.trimIndent()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        }
    }

    @Test
    fun `ChannelMessage Json parsing should throw exception if text_description is absent`() {
        val jsonString = """
        {
            "html_description":"<html>htmlDescription</html>",
            "attachment":{
                "file_name":"fileName",
                "file_encoding":"fileEncoding",
                "file_data":"fileData",
                "file_content_type":"fileContentType"
            }
        }
        """.trimIndent()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createObjectFromJsonString(jsonString) { ChannelMessage.parse(it) }
        }
    }
}

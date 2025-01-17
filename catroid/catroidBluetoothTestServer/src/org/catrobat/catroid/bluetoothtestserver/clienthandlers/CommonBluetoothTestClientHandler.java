/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.bluetoothtestserver.clienthandlers;

import org.catrobat.catroid.bluetoothtestserver.BTClientHandler;
import org.catrobat.catroid.bluetoothtestserver.BTServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CommonBluetoothTestClientHandler extends BTClientHandler {

	@Override
	public void handle(DataInputStream inStream, OutputStream outStream) throws IOException {
		byte[] messageLengthBuffer = new byte[1];

		while (true) {
			inStream.readFully(messageLengthBuffer, 0, 1);
			int expectedMessageLength = messageLengthBuffer[0];
			handleClientMessage(expectedMessageLength, inStream, outStream);
		}
	}

	private void handleClientMessage(int expectedMessageLength, DataInputStream inStream, OutputStream outStream) throws IOException {

		BTServer.writeMessage("--> Incomming expected message length (byte): " + expectedMessageLength + "\n");

		byte[] payload = new byte[expectedMessageLength];

		inStream.readFully(payload, 0, expectedMessageLength);
		BTServer.writeMessage("Received message, length (byte): " + expectedMessageLength + "\n");

		byte[] testResult = payload;

		BTServer.writeMessage("<-- Sending reply message \n");
		outStream.write(new byte[] {(byte) (0xFF & testResult.length)});
		outStream.write(testResult);
		outStream.flush();
	}
}

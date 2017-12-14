/*
 * Copyright IBM Corp. 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package application.rest;

/**
 * Represents a Car document stored in Cloudant.
 */

public class Car {
	public String imgData;
	public String fileName;
	/**
	 * Gets the file name for the Car image.
	 * 
	 * @return: fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Gets the Car base64 data.
	 * 
	 * @return Base64 Car data.
	 */
	public String getImgData() {
		return imgData;
	}

	/**
	 * Sets the name for the Car image
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

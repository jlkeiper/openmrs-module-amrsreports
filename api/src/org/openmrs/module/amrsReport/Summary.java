/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.amrsReport;

import org.openmrs.BaseOpenmrsMetadata;

/**
 * An object that will hold a xml to generate a summary for a patient. There are two main components of a xml. <ul> <li>xml, is a velocity xml string
 * to generate the xml metadata of a summary.</li> <li>xslt, is an xslt string to generate the summary file. To generate the pdf file, we use <a
 * href="@linkplain http://xmlgraphics.apache.org/fop/">Apache FOP</a>. So, the xslt string is not just regular xslt, but also xslt with fop in
 * it.</li> </ul> By default, the summary module will pass the logic service to the velocity evaluation context. In the xml, user can call the logic
 * service using:
 * <p/>
 * <pre>
 * ${functions.eval(String)}
 * </pre>
 * <p/>
 * <pre>
 * ${functions.parse(String)}
 * </pre>
 * <p/>
 */
public class Summary extends BaseOpenmrsMetadata {

	private Integer id;

	private String xml;

	private String xslt;

	private Integer revision = 0;

	private Boolean autoGenerate = Boolean.FALSE;

	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(final Integer id) {
		this.id = id;
	}

	public Boolean isAutoGenerated() {
		return getAutoGenerate();
	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(final String xml) {
		this.xml = xml;
	}

	/**
	 * @return the xslt
	 */
	public String getXslt() {
		return xslt;
	}

	/**
	 * @param xslt the xslt to set
	 */
	public void setXslt(final String xslt) {
		this.xslt = xslt;
	}

	/**
	 * Return the value of the revision
	 *
	 * @return the revision
	 */
	public Integer getRevision() {
		return revision;
	}

	/**
	 * Set the revision with the revision value
	 *
	 * @param revision the revision to set
	 */
	public void setRevision(final Integer revision) {
		this.revision = revision;
	}

	/**
	 * @return the preferred
	 */
	public Boolean getAutoGenerate() {
		return autoGenerate;
	}

	/**
	 * @param autoGenerate the preferred to set
	 */
	public void setAutoGenerate(final Boolean autoGenerate) {
		this.autoGenerate = autoGenerate;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SummaryTemplate: " + getName();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;

		final Summary summary = (Summary) o;
		return getId().equals(summary.getId());
	}

	@Override
	public int hashCode() {
		if (this.getId() == null)
			return super.hashCode();
		return getId().hashCode();
	}
}

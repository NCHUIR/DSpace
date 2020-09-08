/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/CILEA/dspace-cris/wiki/License
 */
package org.dspace.app.cris.integration;

import org.dspace.app.cris.model.ResearchObject;
import org.dspace.content.authority.Choices;

public class DOAuthorityForCRIS extends CRISAuthorityForCRIS<ResearchObject>
{

    @Override
    public int getCRISTargetTypeID()
    {   
        return -1;
    }

    @Override
    public Class<ResearchObject> getCRISTargetClass()
    {
        return ResearchObject.class;
    }
    
    @Override
    public String getPublicPath() {
    	return null;
    }

	@Override
	public ResearchObject getNewCrisObject() {
		return new ResearchObject();
	}

	@Override
	public Choices getMatches(String field, String text, int collection, int start, int limit, String locale,
			boolean extra) {
		return getMatches(field, text, collection, start, limit, locale);
	}  

}

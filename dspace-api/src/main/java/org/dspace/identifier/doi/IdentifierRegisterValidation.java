/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.identifier.doi;

import org.dspace.content.DSpaceObject;
import org.dspace.core.Context;

/**
 * 
 * This interface provide implementation of rules to exclude or permit DOI registration
 * 
 * @author Riccardo Fazio (riccardo.fazio at 4science.it)
 *
 */
public interface IdentifierRegisterValidation {
	
	public boolean canRegister(Context context,DSpaceObject dso);

}

package ca.uhn.fhir.jpa.dao.r4;

/*
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2018 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ca.uhn.fhir.jpa.dao.IFhirResourceDaoConceptMap;
import ca.uhn.fhir.jpa.entity.TermConceptMapGroupElementTarget;
import ca.uhn.fhir.jpa.term.IHapiTerminologySvc;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.ConceptMap;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

public class FhirResourceDaoConceptMapR4 extends FhirResourceDaoR4<ConceptMap> implements IFhirResourceDaoConceptMap<ConceptMap> {
	@Autowired
	private IHapiTerminologySvc myHapiTerminologySvc;

	@Override
	public TranslationResult translate(IPrimitiveType<String> theSourceCode, IPrimitiveType<String> theSourceSystem, IPrimitiveType<String> theTargetSystem, RequestDetails theRequestDetails) {
		TranslationResult retVal = new TranslationResult();

		String sourceCode = theSourceCode.getValueAsString();
		String sourceSystem = theSourceSystem.getValueAsString();
		String targetSystem = theTargetSystem.getValueAsString();
		List<TermConceptMapGroupElementTarget> targets = new ArrayList<>();
		if (isNoneBlank(sourceCode, sourceSystem, targetSystem)) {
			targets = myHapiTerminologySvc.translate(sourceCode, sourceSystem, targetSystem);
		}

		if (targets.isEmpty()) {
			retVal.setMatched(false);
			retVal.setMessage("No matches found!");
		} else {
			retVal.setMatched(true);
			retVal.setMessage("Matches found!");
		}

		return retVal;
	}
}

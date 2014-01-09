package org.openmrs.module.amrsreports.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.model.ARTRegimen;
import org.openmrs.module.amrsreports.service.ARTRegimenService;
import org.openmrs.module.amrsreports.web.propertyeditor.ConceptListEditor;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * Controller for ART Regimen form page
 */
@Controller
@RequestMapping("module/amrsreports/regimen.form")
public class ARTRegimenFormController {

	private static final String SUCCESS_VIEW = "redirect:regimen.list";
	private static final String EDIT_VIEW = "module/amrsreports/regimenForm";

	private Log log = LogFactory.getLog(this.getClass());

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		ARTRegimen regimen = (ARTRegimen) binder.getTarget();

		binder.registerCustomEditor(java.util.Collection.class, "drugs",
				new ConceptListEditor(regimen == null ? null : regimen.getDrugs()));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String editRegimen(
			@RequestParam(value = "id", required = false) Integer artRegimenId,
			ModelMap modelMap) {

		ARTRegimen regimen = null;

		if (artRegimenId != null)
			regimen = Context.getService(ARTRegimenService.class).getARTRegimen(artRegimenId);

		if (regimen == null)
			regimen = new ARTRegimen();

		modelMap.put("regimen", regimen);
		modelMap.put("allLines", Arrays.asList(ARTRegimen.LINE_FIRST, ARTRegimen.LINE_SECOND));
		modelMap.put("allAges", Arrays.asList(ARTRegimen.AGE_ADULT, ARTRegimen.AGE_PEDS));

		return EDIT_VIEW;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String saveRegimen(
			@ModelAttribute("regimen") ARTRegimen regimen,
			BindingResult errors,
			HttpServletRequest request) {

		ARTRegimenService service = Context.getService(ARTRegimenService.class);
		HttpSession httpSession = request.getSession();
		String view = null;

		if (request.getParameter("save") != null) {
			service.saveARTRegimen(regimen);
			view = SUCCESS_VIEW;
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Regimen saved");
		}

		// if the user is retiring out the EncounterType
		else if (request.getParameter("retire") != null) {
			String retireReason = request.getParameter("retireReason");
			if (regimen.getId() != null && !(StringUtils.hasText(retireReason))) {
				errors.reject("retireReason", "general.retiredReason.empty");
				return EDIT_VIEW;
			}

			// TODO understand why this is necessary -- name, desc, etc are empty when it comes this way
			regimen = Context.getService(ARTRegimenService.class).getARTRegimen(regimen.getArtRegimenId());

			service.retireARTRegimen(regimen, retireReason);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Regimen successfully retired");

			view = SUCCESS_VIEW;
		}

		// if the user is un-retiring out the EncounterType
		else if (request.getParameter("unretire") != null) {
			String retireReason = request.getParameter("retireReason");

			// TODO understand why this is necessary -- name, desc, etc are empty when it comes this way
			regimen = Context.getService(ARTRegimenService.class).getARTRegimen(regimen.getArtRegimenId());

			service.unretireARTRegimen(regimen);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Regimen successfully unretired");

			view = SUCCESS_VIEW;
		}

		// if the user is purging the encounterType
		else if (request.getParameter("purge") != null) {

			try {
				// TODO understand why this is necessary -- name, desc, etc are empty when it comes this way
				regimen = Context.getService(ARTRegimenService.class).getARTRegimen(regimen.getArtRegimenId());

				service.purgeARTRegimen(regimen);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Regimen successfully purged");
				view = SUCCESS_VIEW;
			} catch (DataIntegrityViolationException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.object.inuse.cannot.purge");
				view = EDIT_VIEW;
			} catch (APIException e) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.general: " + e.getLocalizedMessage());
				view = EDIT_VIEW;
			}
		}

		return view;
	}
}

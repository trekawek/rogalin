package pl.art.mnp.rogalin;

import org.apache.commons.lang.StringUtils;

import com.vaadin.server.ResourceReference;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public final class PathUtils {

	private PathUtils() {
	}

	public static String getServletPath() {
		return VaadinServlet.getCurrent().getServletContext().getContextPath()
				+ VaadinServletService.getCurrentServletRequest().getServletPath();
	}

	public static String getFullUrl(ResourceReference ref) {
		return String.format("%s/%s", getServletPath(), StringUtils.removeStart(ref.getURL(), "app://"));
	}
}

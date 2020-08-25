/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.util;

import org.dspace.browse.BrowseItem;
import org.dspace.content.Bitstream;
import org.dspace.content.Item;
import org.dspace.content.Metadatum;
import org.dspace.content.Thumbnail;
import org.dspace.content.dao.ItemDAO;
import org.dspace.content.dao.ItemDAOFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.discovery.IGlobalSearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class BitstreamDisplayStrategy implements IDisplayMetadataValueStrategy
{
    public BitstreamDisplayStrategy()
    {
    }

    public String getMetadataDisplay(HttpServletRequest hrq, int limit,
            boolean viewFull, String browseType, int colIdx, String field,
            Metadatum[] metadataArray, BrowseItem item, boolean disableCrossLinks, boolean emph) throws JspException
    {
        return getBitstreamMarkup(hrq, item.getID(), item.getHandle());
    }

    public String getMetadataDisplay(HttpServletRequest hrq, int limit,
            boolean viewFull, String browseType, int colIdx, String field,
            Metadatum[] metadataArray, Item item, boolean disableCrossLinks, boolean emph) throws JspException
    {
        return getBitstreamMarkup(hrq, item.getID(), item.getHandle());
    }

    public String getExtraCssDisplay(HttpServletRequest hrq, int limit,
            boolean b, String string, int colIdx, String field,
            Metadatum[] metadataArray, BrowseItem browseItem,
            boolean disableCrossLinks, boolean emph)
            throws JspException
    {
        return null;
    }

    public String getExtraCssDisplay(HttpServletRequest hrq, int limit,
            boolean b, String browseType, int colIdx, String field,
            Metadatum[] metadataArray, Item item, boolean disableCrossLinks,
            boolean emph) throws JspException
    {
        return null;
    }

    /* generate the (X)HTML required to show the thumbnail */
    private String getBitstreamMarkup(HttpServletRequest hrq, int itemID, String handle)
            throws JspException
    {
        try
        {
            Context c = UIUtil.obtainContext(hrq);
            ItemDAO dao = ItemDAOFactory.getInstance(c);
            Bitstream primaryBitstream = dao.getPrimaryBitstream(itemID, "ORIGINAL");

            if (primaryBitstream == null)
            {
                return "-";
            }
            StringBuilder thumbFrag = new StringBuilder();

            String link = hrq.getContextPath() + "/bitstream/" + handle + "/" + primaryBitstream.getSequenceID() + "/" +
                    UIUtil.encodeBitstreamName(primaryBitstream.getName(), Constants.DEFAULT_ENCODING);
            thumbFrag.append("<a target=\"_blank\" href=\"").append(link)
                    .append("\" ><span aria-hidden=\"true\" class=\"glyphicon glyphicon-file\"></span></a>");
            return thumbFrag.toString();
        }
        catch (SQLException sqle)
        {
            throw new JspException(sqle.getMessage());
        }
        catch (UnsupportedEncodingException e)
        {
            throw new JspException("Server does not support DSpace's default encoding. ", e);
        }
    }
    
    
	@Override
	public String getMetadataDisplay(HttpServletRequest hrq, int limit, boolean viewFull, String browseType,
			int colIdx, String field, Metadatum[] metadataArray, IGlobalSearchResult item, boolean disableCrossLinks,
			boolean emph) throws JspException {
		return getBitstreamMarkup(hrq, item.getID(), item.getHandle());
	}
}

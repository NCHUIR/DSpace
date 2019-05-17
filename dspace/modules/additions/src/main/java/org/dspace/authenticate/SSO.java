/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.authenticate;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * A stackable authentication method
 * based on the DSpace internal "EPerson" database.
 * See the <code>AuthenticationMethod</code> interface for more details.
 * <p>
 * The <em>username</em> is the E-Person's email address,
 * and and the <em>password</em> (given to the <code>authenticate()</code>
 * method) must match the EPerson password.
 * <p>
 * This is the default method for a new DSpace configuration.
 * If you are implementing a new "explicit" authentication method,
 * use this class as a model.
 * <p>
 * You can use this (or another explicit) method in the stack to
 * implement HTTP Basic Authentication for servlets, by passing the
 * Basic Auth username and password to the <code>AuthenticationManager</code>.
 *
 * @author Larry Stone
 * @version $Revision$
 */
public class SSO
        implements AuthenticationMethod {

    /** log4j category */
    private static Logger log = Logger.getLogger(SSO.class);

    /**
     * Look to see if this email address is allowed to register.
     * <p>
     * The configuration key domain.valid is examined
     * in authentication-password.cfg to see what domains are valid.
     * <p>
     * Example - aber.ac.uk domain : @aber.ac.uk
     * Example - MIT domain and all .ac.uk domains: @mit.edu, .ac.uk
     */
    public boolean canSelfRegister(Context context,
                                   HttpServletRequest request,
                                   String email)
            throws SQLException
    {
        // Is there anything set in domain.valid?
        String domains = ConfigurationManager.getProperty("authentication-password", "domain.valid");
        if ((domains == null) || (domains.trim().equals("")))
        {
            // No conditions set, so must be able to self register
            return true;
        }
        else
        {
            // Itterate through all domains
            String[] options = domains.trim().split(",");
            String check;
            email = email.trim().toLowerCase();
            for (int i = 0; i < options.length; i++)
            {
                check = options[i].trim().toLowerCase();
                if (email.endsWith(check))
                {
                    // A match, so we can register this user
                    return true;
                }
            }

            // No match
            return false;
        }
    }

    /**
     *  Nothing extra to initialize.
     */
    public void initEPerson(Context context, HttpServletRequest request,
                            EPerson eperson)
            throws SQLException
    {
    }

    /**
     * We always allow the user to change their password.
     */
    public boolean allowSetPassword(Context context,
                                    HttpServletRequest request,
                                    String username)
            throws SQLException
    {
        return true;
    }

    /**
     * This is an explicit method, since it needs username and password
     * from some source.
     * @return false
     */
    public boolean isImplicit()
    {
        return false;
    }

    /**
     * Add authenticated users to the group defined in authentication-password.cfg by
     * the login.specialgroup key.
     */
    public int[] getSpecialGroups(Context context, HttpServletRequest request)
    {
        // Prevents anonymous users from being added to this group, and the second check
        // ensures they are password users
        try
        {
            if (!context.getCurrentUser().getMetadata("password").equals(""))
            {
                String groupName = ConfigurationManager.getProperty("authentication-password", "login.specialgroup");
                if ((groupName != null) && (!groupName.trim().equals("")))
                {
                    Group specialGroup = Group.findByName(context, groupName);
                    if (specialGroup == null)
                    {
                        // Oops - the group isn't there.
                        log.warn(LogManager.getHeader(context,
                                "password_specialgroup",
                                "Group defined in modules/authentication-password.cfg login.specialgroup does not exist"));
                        return new int[0];
                    } else
                    {
                        return new int[] { specialGroup.getID() };
                    }
                }
            }
        }
        catch (Exception e) {
            // The user is not a password user, so we don't need to worry about them
        }
        return new int[0];
    }

    /**
     * Check credentials: username must match the email address of an
     * EPerson record, and that EPerson must be allowed to login.
     * Password must match its password.  Also checks for EPerson that
     * is only allowed to login via an implicit method
     * and returns <code>CERT_REQUIRED</code> if that is the case.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @param username
     *  Username (or email address) when method is explicit. Use null for
     *  implicit method.
     *
     * @param password
     *  Password for explicit auth, or null for implicit method.
     *
     * @param realm
     *  Realm is an extra parameter used by some authentication methods, leave null if
     *  not applicable.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @return One of:
     *   SUCCESS, BAD_CREDENTIALS, CERT_REQUIRED, NO_SUCH_USER, BAD_ARGS
     * <p>Meaning:
     * <br>SUCCESS         - authenticated OK.
     * <br>BAD_CREDENTIALS - user exists, but assword doesn't match
     * <br>CERT_REQUIRED   - not allowed to login this way without X.509 cert.
     * <br>NO_SUCH_USER    - no EPerson with matching email address.
     * <br>BAD_ARGS        - missing username, or user matched but cannot login.
     */
    public int authenticate(Context context,
                            String username,
                            String password,
                            String realm,
                            HttpServletRequest request)
            throws SQLException
    {
        if (username != null && password != null)
        {
            EPerson eperson = null;
            log.info(LogManager.getHeader(context, "authenticate", "attempting password auth of user="+username));
            try
            {
                String user_collection = SSO.user_check(username, password);
                if(user_collection != null) {
                    creat_user(username, password);
                    String newName = URLDecoder.decode(user_collection, Constants.DEFAULT_ENCODING);
                    log.debug("groupName: " + newName);
                    Group group = Group.find(context, 17);
                    log.debug(group);
                    eperson = EPerson.findByEmail(context, username.toLowerCase());
                    group.addMember(eperson);
                    group.update();
                    context.commit();
                }
                
                eperson = EPerson.findByEmail(context, username.toLowerCase());
            }
            catch (AuthorizeException e)
            {
                log.trace("Failed to authorize looking up EPerson", e);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            if (eperson == null)
            {
                // lookup failed.
                return NO_SUCH_USER;
            }
            else if (!eperson.canLogIn())
            {
                // cannot login this way
                return BAD_ARGS;
            }
            else if (eperson.getRequireCertificate())
            {
                // this user can only login with x.509 certificate
                log.warn(LogManager.getHeader(context, "authenticate", "rejecting PasswordAuthentication because "+username+" requires certificate."));
                return CERT_REQUIRED;
            }
            else if (eperson.checkPassword(password))
            {
                // login is ok if password matches:
                context.setCurrentUser(eperson);
                log.info(LogManager.getHeader(context, "authenticate", "type=PasswordAuthentication"));
                return SUCCESS;
            }
            else
            {
                return BAD_CREDENTIALS;
            }
        }

        // BAD_ARGS always defers to the next authentication method.
        // It means this method cannot use the given credentials.
        else
        {
            return BAD_ARGS;
        }
    }

    /**
     * Returns URL of password-login servlet.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @param request
     *  The HTTP request that started this operation, or null if not applicable.
     *
     * @param response
     *  The HTTP response from the servlet method.
     *
     * @return fully-qualified URL
     */
    public String loginPageURL(Context context,
                               HttpServletRequest request,
                               HttpServletResponse response)
    {
        return response.encodeRedirectURL(request.getContextPath() +
                "/password-login");
    }

    /**
     * Returns message key for title of the "login" page, to use
     * in a menu showing the choice of multiple login methods.
     *
     * @param context
     *  DSpace context, will be modified (EPerson set) upon success.
     *
     * @return Message key to look up in i18n message catalog.
     */
    public String loginPageTitle(Context context)
    {
        return "org.dspace.eperson.PasswordAuthentication.title";
    }

    public static String user_check(String username, String password) {

        HashMap collections = new HashMap();
        HashMap eperson = new HashMap();

        collections.put("ZZ" , "圖書館");
        collections.put("AA" , "農業暨自然資源學院");
        collections.put("AB" , "農藝學系");
        collections.put("AC" , "園藝學系");
        collections.put("AD" , "森林學系");
        collections.put("AE" , "應用經濟學系");
        collections.put("AF" , "植物病理學系");
        collections.put("AG" , "昆蟲學系");
        collections.put("AH" , "動物科學系");
        collections.put("AI" , "土壤環境科學系");
        collections.put("AJ" , "水土保持學系");
        collections.put("AK" , "食品暨應用生物科技學系");
        collections.put("AL" , "生物產業機電工程學系");
        collections.put("AM" , "景觀與遊憩學士學位學程");
        collections.put("AN" , "生物科技學士學位學程");
        collections.put("BA" , "生物科技學研究所");
        collections.put("BC" , "生物產業管理研究所");
        collections.put("CA" , "理學院");
        collections.put("CB" , "化學系");
        collections.put("CC" , "應用數學系");
        collections.put("CD" , "物理學系");
        collections.put("CE" , "資訊科學與工程學系");
        collections.put("DA" , "資訊網路與多媒體研究所");
        collections.put("DB" , "奈米科學研究所");
        collections.put("DC" , "統計學研究所");
        collections.put("EA" , "工學院");
        collections.put("EB" , "土木工程學系");
        collections.put("EC" , "機械工程學系");
        collections.put("ED" , "環境工程學系");
        collections.put("EE" , "電機工程學系");
        collections.put("EF" , "化學工程學系");
        collections.put("EG" , "材料科學與工程學系");
        collections.put("FA" , "精密工程研究所");
        collections.put("FB" , "通訊工程研究所");
        collections.put("FC" , "光電工程研究所");
        collections.put("FD" , "生醫工程研究所");
        collections.put("GA" , "生命科學院");
        collections.put("GB" , "生命科學系");
        collections.put("HA" , "分子生物學研究所");
        collections.put("HB" , "生物化學研究所");
        collections.put("HC" , "生物醫學研究所");
        collections.put("HD" , "基因體暨生物資訊學研究所");
        collections.put("IA" , "獸醫學院");
        collections.put("IB" , "獸醫學系");
        collections.put("JA" , "微生物暨公共衛生學研究所");
        collections.put("KA" , "文學院");
        collections.put("KB" , "中文系");
        collections.put("KC" , "外文系");
        collections.put("KD" , "歷史學系");
        collections.put("LA" , "圖書資訊學研究所");
        collections.put("MA" , "管理學院");
        collections.put("MB" , "財務金融學系");
        collections.put("MD" , "企業管理學系");
        collections.put("MG" , "會計學系");
        collections.put("MF" , "資訊管理學系");
        collections.put("ME" , "行銷學系");
        collections.put("NB" , "科技管理研究所");
        collections.put("ND" , "運動與健康管理研究所");
        collections.put("NF" , "高階經理人碩士在職專班");
        collections.put("OH" , "高階經理人碩士在職專班");
        collections.put("MC" , "法律學系");
        collections.put("NE" , "教師專業發展研究所");
        collections.put("NA" , "國際政治研究所");
        collections.put("NC" , "國家政策與公共事務研究所");
        collections.put("WG" , "研究發展處");
        collections.put("WI" , "體育室");
        collections.put("WL" , "計算機及資訊網路中心");
        collections.put("WM" , "師資培育中心");
        collections.put("WN" , "校友聯絡中心");
        collections.put("WZ" , "農業推廣中心");
        collections.put("XE" , "動物疾病診斷中心");
        collections.put("XF" , "生物科技發展中心");
        collections.put("XG" , "奈米科技中心");
        collections.put("XQ" , "通識教育中心");
        eperson.put("07" , "職員");
        eperson.put("08" , "專任教師");
        eperson.put("09" , "兼任人員");

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse("http://webpac.lib.nchu.edu.tw/X?op=bor-auth&library=top51&bor_id=" + username + "&verification=" + password);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("bor-auth");
            for (int i = 0; i < nList.getLength(); i++)
            {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    try
                    {
                        if ((Integer.parseInt((getTagValue("z305-bor-status", eElement))) == 7) ||
                            (Integer.parseInt((getTagValue("z305-bor-status", eElement))) == 8) ||
                            (Integer.parseInt((getTagValue("z305-bor-status", eElement))) == 9))
                        {
                            return String.valueOf(collections.get(getTagValue("z305-bor-type", eElement)));
                        }
                        else
                        {
                            log.debug("您的身分並不是職員、專任教師、兼任人員");
                        }
                    }
                    catch (Exception e)
                    {
                        log.debug("驗證失敗");
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    public static void creat_user (String username,  String password) throws SQLException
    {
        log.debug("creat user");
        Context context = new Context();

        // Disable authorization since this only runs from the local commandline.
        context.turnOffAuthorisationSystem();

        EPerson eperson = null;
        try
        {
            eperson = EPerson.create(context);
        } catch (SQLException ex)
        {
            context.abort();
            System.err.println(ex.getMessage());
        }
        catch (AuthorizeException ex)
        {
            /* XXX SNH */
        }
        eperson.setCanLogIn(true);
        eperson.setSelfRegistered(false);

        eperson.setEmail(username);
        eperson.setLanguage("zh_TW");
        eperson.setNetid(username);
        eperson.setPassword(password);

        try
        {
            eperson.update();
            context.commit();
            context.setCurrentUser(eperson);
            System.out.printf("Created EPerson %d\n", eperson.getID());
        }
        catch (SQLException ex)
        {
            context.abort();
            System.err.println(ex.getMessage());
        }
        catch (AuthorizeException ex)
        {
            /* XXX SNH */
        }
    }
}

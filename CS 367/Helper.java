import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * A few helper static methods
 *
 * @author jmishra
 */
public class Helper
{

    /**
     * Given a nickname as a String return the User object from the List of
     * Users
     *
     * @param users the list of users from which a user will be returned
     * @param nickname the nickname of the user to be returned
     * @return the USer object whose nickname is nickname and this object is
     * searched from the users List
     */
    public static User getUserFromNickname(List<User> users, String nickname)
    {
    	//a iterator to check everyone in the List users
        Iterator <User> itr = users.iterator();
    	User user;
    	while(itr.hasNext()){
    		user = itr.next();
    		//check whether the nickname is the name in the list
    		if(user.getNickname().equals(nickname)){
    			//if the nicknames are the same, return the user
    			return user;
    		}
    	}
        return null;
    }

    /**
     * Given a nickname as a String return the BroadcastList from the List of
     * BroadcastListswhile(itr.hasNext()){
    		BroadcastList list = itr.next();
    		if(list.getNickname().equals(nickname)){
    			return true;
    		}
     *
     * @param lists the list of lists from which one list object is to be
     * returned
     * @param nickname the nickname of the broadcast list to be returned
     * @return the BroadcastList object whose nickname is nickname. This object
     * is searched from the lists List
     */
    public static BroadcastList getBroadcastListFromNickname(
            List<BroadcastList> lists, String nickname)
    {
    	//a iterator to check broadcastList from broadcastLists list
    	Iterator <BroadcastList> itr = lists.iterator();
    	BroadcastList list;
    	while(itr.hasNext()){
    		list = itr.next();
    		//check whether the nickname is the name in the list
    		if(list.getNickname().equals(nickname)){
    			return list;
    		}
    	}
        return null;
    }

    /**
     * Given a nickname determine whether a User exists globally with that
     * nickname
     *
     * @param nickname the nickname to check for
     * @return true if a user exists globally with the provided nickname
     */
    public static boolean isExistingGlobalContact(String nickname)
    {
    	//get a list of all users 
    	List <User>	allUsers = Config.getInstance().getAllUsers();
    	//check every user in the allUsers List
    	Iterator <User> itr = allUsers.iterator();
    	while(itr.hasNext()){
    		User user = itr.next();
    		if(user.getNickname().equals(nickname)){
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Populate data from the file with the given path
     *
     * @param filename the path to the input file
     * @throws FileNotFoundException this is thrown if the file is not found
     * @throws IOException this is thrown if some error occurs while reading the
     * input file
     * @throws WhatsAppRuntimeException this is thrown if any invalid data is
     * used to construct any of the WhatsApp objects or a line is encountered
     * that does not begin with any of the four words mentioned in the
     * specification
     * @throws java.text.ParseException this exception is thrown if some issue
     * occurs while parsing the date string
     */
    public static void populateData(String filename)
    		throws FileNotFoundException, IOException, WhatsAppRuntimeException,
    		ParseException
    {
        Scanner scanner = new Scanner(new File(filename));        
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            switch (parts[0])
            {
                case "user":
                    User user = new User(parts[1], parts[2], parts[3], parts[4],
                    		new ArrayList<>(), new ArrayList<>(),
                    		new ArrayList<>());
                    Config.getInstance().getAllUsers().add(user);
                    break;
                case "flist":
                    user = getUserFromNickname(Config.getInstance().
                            getAllUsers(), parts[1]);
                    for (int i = 2; i < parts.length; ++i)
                    {
                        user.getFriends().add(getUserFromNickname(Config.
                                getInstance().getAllUsers(), parts[i]));
                    }
                    break;
                case "bcast":
                    user = getUserFromNickname(Config.getInstance().
                            getAllUsers(), parts[1]);
                    BroadcastList newList = new BroadcastList(parts[3],
                    		new ArrayList<>());
                    for (int i = 4; i < parts.length; ++i)
                    {
                        newList.getMembers().add(parts[i]);
                    }
                    user.getBroadcastLists().add(newList);
                    break;
                case "message":
                    SimpleDateFormat sdf = new SimpleDateFormat
                    ("MM/dd/yyyy HH:mm:ss");
                    if ((getUserFromNickname(Config.
                            getInstance().getAllUsers(), parts[1])).
                            isBroadcastList(parts[2]))
                    {
                        Message sentMessage = 
                        		new Message(parts[1], null, parts[2], sdf.
                                parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), true);
                        getUserFromNickname
                        (Config.getInstance().getAllUsers(), parts[1]).
                                getMessages().add(sentMessage);
                        Iterator<String> memberIterator = 
                        		getBroadcastListFromNickname
                        		(getUserFromNickname(Config.
                                getInstance().getAllUsers(), parts[1]).
                                getBroadcastLists(), parts[2]).getMembers().
                                iterator();
                        for (int i = 5; i < parts.length; ++i)
                        {
                            String member = memberIterator.next();
                            Message receivedMessage = new Message(parts[1],
                            		member, null, sdf.
                                    parse(parts[3]), parts[4].
                                    substring(1, parts[4].length() - 1),
                                    parts[i].equals("read"));
                            getUserFromNickname(Config.getInstance().
                                    getAllUsers(), member).
                                    getMessages().add(receivedMessage);
                        }
                    } else
                    {
                        Message sentMessage = new Message(parts[1], parts[2],
                        		null, sdf.parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), true);
                        getUserFromNickname(Config.getInstance().getAllUsers(),
                        		parts[1]).getMessages().add(sentMessage);
                        Message receivedMessage =
                        		new Message(parts[1], parts[2], null, sdf.
                                parse(parts[3]), parts[4].
                                substring(1, parts[4].length() - 1), parts[5].
                                equals("read"));
                        getUserFromNickname
                        (Config.getInstance().getAllUsers(), parts[2]).
                                getMessages().add(receivedMessage);
                    }
                    break;
                default:
                    throw new WhatsAppRuntimeException();
            }
        }
    }

}

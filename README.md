# dev-team-mngr service

Tool for managing team-mates.

### User model 

The data model describing each user is defined by the following properties: 
 * id
 * firstname
 * surname
 * position
 * GitHub profile url
 
### Integration with GitHub

The integration with the GitHub API is obtained through the following external library:
 * [GitHub API for Java][1] <br />
    Copyright (c) 2011- Kohsuke Kawaguchi and other contributors <br />
    Licensed under [MIT][2] License
     

For an authenticated access to the GitHub API create the '~/.github' property file containing either the login/password information or an authentication token in the following form:
 
login=\<username> <br />
password=\<password>   

or

oauth=\<authentication token>







[1]: http://github-api.kohsuke.org/
[2]: https://opensource.org/licenses/mit-license.php
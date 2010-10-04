/* $Id: VersionDoc.java 988245 2010-08-23 18:39:35Z kwright $ */

/**
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements. See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.manifoldcf.crawler.connectors.livelink;

import org.apache.manifoldcf.core.interfaces.*;

public class VersionDoc
{
        public static final String _rcsid = "@(#)$Id: VersionDoc.java 988245 2010-08-23 18:39:35Z kwright $";

        private VersionDoc()
        {
        }


        public static void main(String[] args)
        {
                if (args.length != 6)
                {
                        System.err.println("Usage: VersionDoc <servername> <port> <username> <password> <livelinkpath> <filepath>");
                        System.exit(1);
                }

                try
                {
                        LiveLinkSupport handle = new LiveLinkSupport(args[0],new Integer(args[1]).intValue(),args[2],args[3]);
                        try
                        {
                                String id = handle.modifyDocument(args[4],args[5]);
                                UTF8Stdout.print(id);
                        }
                        finally
                        {
                                handle.close();
                        }
                        System.err.println("Successfully revised");
                }
                catch (ManifoldCFException e)
                {
                        e.printStackTrace(System.err);
                        System.exit(2);
                }
        }

}

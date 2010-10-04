/* $Id: ExpireStufferThread.java 988245 2010-08-23 18:39:35Z kwright $ */

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
package org.apache.manifoldcf.crawler.system;

import org.apache.manifoldcf.core.interfaces.*;
import org.apache.manifoldcf.agents.interfaces.*;
import org.apache.manifoldcf.crawler.interfaces.*;
import org.apache.manifoldcf.crawler.system.Logging;
import java.util.*;
import java.lang.reflect.*;

/** This class represents the stuffer thread.  This thread's job is to request documents from the database and add them to the
* document queue.  The thread then sleeps until the document queue is empty again.
*/
public class ExpireStufferThread extends Thread
{
  public static final String _rcsid = "@(#)$Id: ExpireStufferThread.java 988245 2010-08-23 18:39:35Z kwright $";

  // Local data
  // This is a reference to the static main document expiration queue
  protected DocumentDeleteQueue documentQueue;
  /** Worker thread pool reset manager */
  protected WorkerResetManager resetManager;
  // This is the number of entries we want to stuff at any one time.
  protected int n;

  /** Constructor.
  *@param documentQueue is the document queue we'll be stuffing.
  *@param n represents the number of threads that will be processing queued stuff, NOT the
  * number of documents to be done at once!
  */
  public ExpireStufferThread(DocumentDeleteQueue documentQueue, int n, WorkerResetManager resetManager)
    throws ManifoldCFException
  {
    super();
    this.documentQueue = documentQueue;
    this.n = n;
    this.resetManager = resetManager;
    setName("Expire stuffer thread");
    setDaemon(true);
    // The priority of this thread is higher than most others.  We want stuffing to proceed even if the machine
    // is pretty busy already.
    setPriority(getPriority()+1);
  }

  public void run()
  {
    resetManager.registerMe();

    try
    {
      // Create a thread context object.
      IThreadContext threadContext = ThreadContextFactory.make();
      IJobManager jobManager = JobManagerFactory.make(threadContext);

      Logging.threads.debug("Expire stuffer thread: Maximum document count per check is "+Integer.toString(n));

      // Hashmap keyed by jobid and containing ArrayLists.
      // This way we can guarantee priority will do the right thing, because the
      // priority is per-job.  We CANNOT guarantee anything about scheduling order, however,
      // other than that it falls in the time window.
      HashMap documentSets = new HashMap();

      // Job description map (local) - designed to improve performance.
      // Cleared and reloaded on every batch of documents.
      HashMap jobDescriptionMap = new HashMap();

      IDBInterface database = DBInterfaceFactory.make(threadContext,
        ManifoldCF.getMasterDatabaseName(),
        ManifoldCF.getMasterDatabaseUsername(),
        ManifoldCF.getMasterDatabasePassword());

      int deleteChunkSize = database.getMaxInClause();

      // Loop
      while (true)
      {
        // Do another try/catch around everything in the loop
        try
        {
          if (Thread.currentThread().isInterrupted())
            throw new ManifoldCFException("Interrupted",ManifoldCFException.INTERRUPTED);

          // Check if we're okay
          resetManager.waitForReset(threadContext);

          // System.out.println("Waiting...");
          // Wait until queue is empty enough.
          boolean isEmpty = documentQueue.checkIfEmpty(n*3);
          if (isEmpty == false)
          {
            sleep(1000);
            continue;
          }

          Logging.threads.debug("Expiration stuffer thread woke up");

          // What we want to do is load enough documents to completely fill n queued document sets.
          // The number n passed in here thus cannot be used in a query to limit the number of returned
          // results.  Instead, it must be factored into the limit portion of the query.
          long currentTime = System.currentTimeMillis();
          DocumentDescription[] descs = jobManager.getExpiredDocuments(deleteChunkSize,currentTime);

          if (Thread.currentThread().isInterrupted())
            throw new ManifoldCFException("Interrupted",ManifoldCFException.INTERRUPTED);

          if (Logging.threads.isDebugEnabled())
          {
            Logging.threads.debug("Expiration stuffer thread: Found "+Integer.toString(descs.length)+" documents to expire");
          }

          // If there are no documents at all, then we can sleep for a while.
          // The theory is that we need to allow stuff to accumulate.
          if (descs.length == 0)
          {
            ManifoldCF.sleep(60000L);      // 1 minute
            continue;
          }

          // Do the stuffing
          DeleteQueuedDocument[] docDescs = new DeleteQueuedDocument[descs.length];
          int k = 0;
          while (k < docDescs.length)
          {
            docDescs[k] = new DeleteQueuedDocument(descs[k]);
            k++;
          }
          DocumentDeleteSet set = new DocumentDeleteSet(docDescs);
          documentQueue.addDocuments(set);

          // If we don't wait here, the other threads don't seem to have a chance to queue anything else up.
          ManifoldCF.sleep(1000L);
        }
        catch (ManifoldCFException e)
        {
          if (e.getErrorCode() == ManifoldCFException.INTERRUPTED)
            break;

          if (e.getErrorCode() == ManifoldCFException.DATABASE_CONNECTION_ERROR)
          {
            resetManager.noteEvent();
            documentQueue.reset();

            Logging.threads.error("Expiration stuffer thread aborting and restarting due to database connection reset: "+e.getMessage(),e);
            try
            {
              // Give the database a chance to catch up/wake up
              ManifoldCF.sleep(10000L);
            }
            catch (InterruptedException se)
            {
              break;
            }
            continue;
          }

          // Log it, but keep the thread alive
          Logging.threads.error("Exception tossed: "+e.getMessage(),e);

          if (e.getErrorCode() == ManifoldCFException.SETUP_ERROR)
          {
            System.exit(1);
          }

        }
        catch (InterruptedException e)
        {
          // We're supposed to quit
          break;
        }
        catch (OutOfMemoryError e)
        {
          System.err.println("agents process ran out of memory - shutting down");
          e.printStackTrace(System.err);
          System.exit(-200);
        }
        catch (Throwable e)
        {
          // A more severe error - but stay alive
          Logging.threads.fatal("Error tossed: "+e.getMessage(),e);
        }
      }
    }
    catch (Throwable e)
    {
      // Severe error on initialization
      System.err.println("agents process could not start - shutting down");
      Logging.threads.fatal("ExpirationStufferThread initialization error tossed: "+e.getMessage(),e);
      System.exit(-300);
    }
  }

}

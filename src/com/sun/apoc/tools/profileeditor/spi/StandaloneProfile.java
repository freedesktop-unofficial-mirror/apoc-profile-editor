/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 2 only ("GPL") or
 * the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy
 * of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 * License for the specific language governing permissions and
 * limitations under the License. When distributing the software,
 * include this License Header Notice in each file and include
 * the License file at /legal/license.txt. If applicable, add the
 * following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your
 * decision by adding "[Contributor] elects to include this
 * software in this distribution under the [CDDL or GPL
 * Version 2] license." If you don't indicate a single choice
 * of license, a recipient has the option to distribute your
 * version of this file under either the CDDL, the GPL Version
 * 2 or to extend the choice of license to its licensees as
 * provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such
 * option by the copyright holder.
 */

package com.sun.apoc.tools.profileeditor.spi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.sun.apoc.spi.IllegalReadException;
import com.sun.apoc.spi.IllegalWriteException;
import com.sun.apoc.spi.PolicySource;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.policies.PolicyInfo;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.InvalidPriorityException;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.apoc.spi.profiles.ProfileStreamException;
import com.sun.apoc.spi.profiles.ProfileZipException;

import com.sun.apoc.tools.profileeditor.spi.ZipProfileReadWrite;

/**
 *
 * @author cs202741
 */
public class StandaloneProfile implements Profile{
    
    // field variables
    private ProfileRepository mRepository;
    private PolicySource mPolicySource;
    private String mDisplayName;
    private Map mPolicyCache;
    private URL mProfileURL;
    private String mComment;
    private String mAuthor;
    private int mPriority;
    private String mId;
    private long mLastModified;

    private Applicability mApplicability;
    
    public static Profile loadProfile( String aId, ProfileRepository aRepository,
        PolicySource aPolicySource ) throws SPIException {
        
        Applicability aApplicability = Applicability.USER;
        String displayName = null;
        String author = "";
        String comment = "";
        int priority = 0;
        Profile profile;
        URL profileURL;
        
        profile = null;
        profileURL = null;
        
        profileURL = getProfileURL( aId, aRepository );
        /*
        metaData = getMetaData( profileURL );
        if ( metaData.containsKey( DISPLAYNAME ) ) {
            displayName = (String) metaData.get( DISPLAYNAME );
        }
         
        if ( metaData.containsKey( COMMENT ) ) {
            comment = (String) metaData.get( COMMENT );
        }
         
        if ( metaData.containsKey( APPLICABILITY ) ) {
            String applicabilityStr = (String) metaData.get( APPLICABILITY );
            applicability = Applicability.getApplicability( applicabilityStr );
        }
        if ( metaData.containsKey( PRIORITY ) ) {
            String priorityStr = (String) metaData.get( PRIORITY );
            priority = new Integer( priorityStr ).intValue();
        }
         
        if ( metaData.containsKey( AUTHOR ) ) {
            author = (String)metaData.get( AUTHOR );
        }
         */
        profile = new StandaloneProfile( aId, aRepository, displayName, comment, aApplicability,
                priority, profileURL, author, true );
        
        return profile;
    }
    
    public static Profile createNewProfile( ProfileRepository aRepository,
            String aDisplayName, Applicability aApplicability, int aPriority ) throws SPIException {
        URL profileURL;
        String author;
        Profile profile = null;
        
        if ( aRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }

        profileURL = getProfileURL( aRepository.getId(), aRepository );
        
        author = "Author, Test";
        
        profile = new StandaloneProfile( aRepository.getId(), aRepository, aDisplayName, "",
                aApplicability, aPriority, profileURL, author, false );
        
        ((StandaloneProfile)profile).writeProfile();
        
        return profile;
    }
    
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#setPriority(int)
     */
    public void setPriority(int aPriority) throws SPIException {
//        if (((StandaloneProfileRepositoryImpl)mRepository).priorityExists( aPriority ) ) {
//            throw new InvalidPriorityException(
//                    InvalidPriorityException.USED_PRIORITY_KEY, aPriority);
//        }
        
        mPriority = aPriority;
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#setDisplayName(java.lang.String)
     */
    public void setDisplayName(String aDisplayName) throws SPIException {
        if (aDisplayName == null) {
            throw new IllegalArgumentException();
        }
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        mDisplayName = aDisplayName;
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#storePolicy(com.sun.apoc.spi.policies.Policy)
     */
    public void storePolicy(Policy aPolicy) throws SPIException {
        if (aPolicy == null) {
            throw new IllegalArgumentException();
        }
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        
        String id = aPolicy.getId();
        
        if ( mPolicyCache.containsKey( id ) ) {
            mPolicyCache.remove( id );
        }
        
	if ( aPolicy.getData() != null) {
	    Policy policy = aPolicy;
	    if ( aPolicy.getProfileId() == null ) {
		/* #6283807# policies created with no profileId should get one when
		 * they are stored
		 */
		policy = new Policy(aPolicy.getId(), this.getId(), aPolicy.getData(), aPolicy.getLastModified());
	    }
	    mPolicyCache.put( id , policy );
        }
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#destroyPolicy(com.sun.apoc.spi.policies.Policy)
     */
    public void destroyPolicy(Policy aPolicy) throws SPIException {
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        
        String id = aPolicy.getId();
        
        if (mPolicyCache.containsKey( id )) {
            mPolicyCache.remove( id );
        }
        
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#hasPolicies()
     */
    public boolean hasPolicies() throws SPIException {
        return ( mPolicyCache.size() != 0 );
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPolicies()
     */
    public Iterator getPolicies() throws SPIException {
        return mPolicyCache.values().iterator();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPolicies(java.util.ArrayList)
     */
    public Iterator getPolicies(ArrayList aPolicyIdList) throws SPIException {
        LinkedList policyList = new LinkedList();
        Iterator it = aPolicyIdList.iterator();
        String policyId;
        
        while ( it.hasNext() ) {
            policyId = (String) it.next();
            if ( mPolicyCache.containsKey( policyId ) ) {
                policyList.add( mPolicyCache.get( policyId ));
            }
        }
        
        return policyList.iterator();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPolicy(java.lang.String)
     */
    public Policy getPolicy(String aId) throws SPIException {
        if (aId == null) {
            throw new IllegalArgumentException();
        }
        Policy policy = null;
        
        if ( mPolicyCache.containsKey( aId )) {
            policy = (Policy)mPolicyCache.get( aId);
        }
        
        return policy;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#hasAssignedEntities()
     */
    public boolean hasAssignedEntities() throws SPIException {
        return mPolicySource.getAssignmentProvider().getAssignedEntities( this ).hasNext();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getAssignedEntities()
     */
    public Iterator getAssignedEntities() throws SPIException {
        return mPolicySource.getAssignmentProvider().getAssignedEntities( this );
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#setApplicability(com.sun.apoc.spi.profiles.Applicability)
     */
    public void setApplicability(Applicability aApplicability)
    throws SPIException {
        if (aApplicability == null) {
            throw new IllegalArgumentException();
        }
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        
        mApplicability = aApplicability;
        
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#setComment(java.lang.String)
     */
    public void setComment(String aComment) throws SPIException {
        if (aComment == null) {
            throw new IllegalArgumentException();
        }
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        
        mComment = aComment;
        writeProfile();
    }
    
    public void setAuthor(String author) throws SPIException {
        if(author == null){
            author = new String("");
        }
        mAuthor = author;
        writeProfile();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getAuthor()
     */
    public String getAuthor() throws SPIException {
        if (mAuthor == null) {
            return new String("");
        }
        return mAuthor;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object aProfile) {
        Profile profile;
        
        profile = (Profile)aProfile;
        return mId.equals( profile.getId() );
    }
    
    private StandaloneProfile( String aId, ProfileRepository aRepository, String aDisplayName,
            String aComment, Applicability aApplicability, int aPriority, URL aProfileURL,
            String author, boolean aStored ) throws SPIException {
        mApplicability = aApplicability;
        mDisplayName = aDisplayName;
        mRepository = aRepository;
        mProfileURL = aProfileURL;
        mPriority = aPriority;
        mComment = aComment;
        mAuthor = author;
        mId = aId;
        
        mPolicyCache = (Map)Collections.synchronizedMap( new HashMap() );
        
        if ( aStored ) {
            readPolicy(); // sets the lastModified from the zip entries
        } else {
            mLastModified = System.currentTimeMillis();
        }
    }
    
    private void readPolicy() throws SPIException {
        ZipInputStream input = null;
        try {
            input = new ZipInputStream(mProfileURL.openStream());
            // first try to read the meta configuration data
            Properties metaData = ZipProfileReadWrite.readMetaData(input);
            Iterator it = null;
            if (metaData != null) {
                // then try to load the policy data
                it = ZipProfileReadWrite.readPolicies(input);
            } else {
                // if meta data entry does not exist, we assume the old
                // profile/policy format
                it = ZipProfileReadWrite.readOldPoliciesFormat(input);
                // if 'it' contains no data that means it is either an old
                // format source but with no policies in it or that the source
                // doesn't have the required format (new or old)
                // in both cases, raise an exception.
                if (!it.hasNext()) {
                    input.close();
                    throw new ProfileStreamException(ProfileStreamException.EMPTY_STREAM_KEY);
                }
            }
            input.close();
            
            // store the policies
            Policy policy;
            mLastModified = 0L;
            while(it.hasNext()) {
                policy = (Policy)it.next();
                mPolicyCache.put( policy.getId(), new Policy( policy.getId(), mId, policy.getData(), policy.getLastModified()) );
                // Although the last modified should be defined in setMetaData
                // it is possible that some profiles exists without the 
                // lastModified tag in the metadata - SunITOps pre FCS APOC 2.0 
                // deployments - The following if calcualtes the lastModified 
                // bit
                if ( mLastModified < policy.getLastModified() ) {
                    mLastModified = policy.getLastModified();
                }
            }
            
            // update the meta data
            setMetaData(metaData);
        } catch (ZipException ze) {
            try {
                input.close();
            } catch (Exception ignored) {}
            throw new ProfileZipException(ze);
        } catch (IOException ioe) {
            try {
                input.close();
            } catch (Exception ignored) {}
            //throw new ProfileStreamException(
            //        ProfileStreamException.ERROR_STREAM_KEY, ioe);
            throw new SPIException();
        }
    }
    
    
    private void writeProfile() throws SPIException {
        FileOutputStream destFile = null;
        
        if ( mRepository.isReadOnly() ) {
            throw new IllegalWriteException();
        }
        try {
            // create the profile repository
            //((StandaloneProfileRepositoryImpl)mRepository).createStorage();
            destFile = new FileOutputStream( mProfileURL.getPath() );
        } catch (FileNotFoundException e) {
            throw new IllegalWriteException(
                    IllegalWriteException.FILE_WRITE_KEY, e);
        }
        
        // Write data to the file
        ZipOutputStream output = new ZipOutputStream(destFile);
        try {
            mLastModified = System.currentTimeMillis();
            ZipProfileReadWrite.writeMetaData(this, output);
            ZipProfileReadWrite.writePolicies(this, output);
            output.close();
            destFile.close();
        } catch (ZipException ze) {
            try {
                output.close();
                destFile.close();
            } catch (Exception ignored) {}
            throw new ProfileZipException(ze);
            
        } catch (IOException ioe) {
            try {
                output.close();
                destFile.close();
            } catch (Exception ignored) {}
            throw new ProfileStreamException(
                    ProfileStreamException.ERROR_STREAM_KEY, ioe);
        }
    }
    
    private void setMetaData(Properties aMetaData ) {
        if ( aMetaData != null ) {
            if ( aMetaData.containsKey( ZipProfileReadWrite.DISPLAY_NAME ) ) {
                mDisplayName = (String) aMetaData.get( ZipProfileReadWrite.DISPLAY_NAME );
            }
            
            if ( aMetaData.containsKey( ZipProfileReadWrite.COMMENT ) ) {
                mComment = (String) aMetaData.get( ZipProfileReadWrite.COMMENT );
            }
            
            if ( aMetaData.containsKey( ZipProfileReadWrite.APPLICABILITY ) ) {

            }
            if ( aMetaData.containsKey( ZipProfileReadWrite.PRIORITY ) ) {
                String priorityStr = (String) aMetaData.get( ZipProfileReadWrite.PRIORITY );
                mPriority = new Integer( priorityStr ).intValue();
            }
            
            if ( aMetaData.containsKey( ZipProfileReadWrite.AUTHOR ) ) {
                mAuthor = (String)aMetaData.get( ZipProfileReadWrite.AUTHOR );
            }
            if ( aMetaData.containsKey( ZipProfileReadWrite.LAST_MODIFIED )) {
                mLastModified = Long.parseLong((String)aMetaData.get( ZipProfileReadWrite.LAST_MODIFIED ));
            }
        }
    }
    
    protected static URL getProfileURL( String aId, ProfileRepository aRepository )
    throws SPIException {
        StringBuffer profileURL;
        String idSplit[];

        profileURL = new StringBuffer( ((StandaloneProfileRepositoryImpl)aRepository).getLocation() );

        if( !profileURL.substring( profileURL.length()-4, profileURL.length() ).equalsIgnoreCase(".zip") ){
            profileURL.append( ".zip" );
        }
        
        URL url = null;
        try {
            url = new URL( profileURL.toString() );
        } catch (MalformedURLException mue) {
            throw new IllegalReadException(
                    IllegalReadException.FILE_READ_KEY, mue);
        }
        return url;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getId()
     */
    public String getId() {
        return mId;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getDisplayName()
     */
    public String getDisplayName() {
        if (mDisplayName == null) {
            return new String("");
        }
        return mDisplayName;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPriority()
     */
    public int getPriority() {
        return mPriority;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getLastModified()
     */
    public long getLastModified() throws SPIException {
        return mLastModified;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getApplicability()
     */
    public Applicability getApplicability() {
        return mApplicability;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getComment()
     */
    public String getComment() throws SPIException {
        if (mComment == null) {
            return new String("");
        }
        return mComment;
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getProfileRepository()
     */
    public ProfileRepository getProfileRepository() {
        return mRepository;
    }
    
    static protected String createId( String aId, String aRepId ) {
        StringBuffer id;
        
        id = new StringBuffer( aRepId );
        id.append( "-");
        id.append( aId);
        
        return id.toString();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPolicies(java.util.Iterator)
     */
    public Iterator getPolicies(Iterator aPolicyIdList) throws SPIException {
        if (aPolicyIdList == null) {
            throw new IllegalArgumentException();
        }
        List policies;
        String id;
        
        policies = new LinkedList();
        while ( aPolicyIdList.hasNext() ) {
            id = (String)aPolicyIdList.next();
            if ( mPolicyCache.containsKey( id ) ) {
                policies.add( mPolicyCache.get( id ) );
            }
        }
        return policies.iterator();
    }
    
    /* (non-Javadoc)
     * @see com.sun.apoc.spi.profiles.Profile#getPolicyInfos(java.util.Iterator)
     */
    public Iterator getPolicyInfos(Iterator aPolicyIdList) throws SPIException {
        if (aPolicyIdList == null) {
            throw new IllegalArgumentException();
        }
        PolicyInfo policyInfo;
        List policiesInfo;
        Policy policy;
        String id;
        
        policiesInfo = new LinkedList();
        while ( aPolicyIdList.hasNext() ) {
            id = (String)aPolicyIdList.next();
            if ( mPolicyCache.containsKey( id ) ) {
                policy = (Policy)mPolicyCache.get( id );
                policyInfo = new PolicyInfo(id, mId, policy.getLastModified() );
                policiesInfo.add( policyInfo);
            }
        }
        
        return policiesInfo.iterator();
    }
}

import React, { useState, ChangeEvent } from 'react';
import axios from 'axios';
import styles from '../styles/UseCaseUploader.module.css';

interface UseCaseResponse {
    domainObjects: string[];
}

const UseCaseUploader: React.FC = () => {
    const [description, setDescription] = useState<string>('');
    const [file, setFile] = useState<File | null>(null);
    const [domainObjects, setDomainObjects] = useState<string[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const [selectedTab, setSelectedTab] = useState<'text' | 'file'>('text');

    const handleTextSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');
        setDomainObjects([]);
        setLoading(true);
        try {
            const response = await axios.post<UseCaseResponse>(
                'http://localhost:8080/api/usecase-service/v1/usecases/text',
                { description }
            );
            setDomainObjects(response.data.domainObjects);
        } catch (err: any) {
            setError('Error processing text input.');
        } finally {
            setLoading(false);
        }
    };

    const handleFileSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!file) return;
        setError('');
        setDomainObjects([]);
        setLoading(true);
        try {
            const formData = new FormData();
            formData.append('file', file);
            const response = await axios.post<UseCaseResponse>(
                'http://localhost:8080/api/usecase-service/v1/usecases/upload',
                formData,
                {
                    headers: {
                        ["Content-Type"]: "multipart/form-data"
                    }
                }
            );

            setDomainObjects(response.data.domainObjects);
        } catch (err: any) {
            setError('Error processing file upload.');
        } finally {
            setLoading(false);
        }
    };

    const onFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files.length > 0) {
            setFile(e.target?.files[0]);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Automated Test Case Generation Tool</h1>
            <div className={styles.tabButtons}>
                <button
                    onClick={() => setSelectedTab('text')}
                    disabled={selectedTab === 'text'}
                    className={`${styles.tabButton} ${selectedTab === 'text' ? styles.active : ''}`}
                >
                    Text Input
                </button>
                <button
                    onClick={() => setSelectedTab('file')}
                    disabled={selectedTab === 'file'}
                    className={`${styles.tabButton} ${selectedTab === 'file' ? styles.active : ''}`}
                >
                    PDF Upload
                </button>
            </div>

            {selectedTab === 'text' && (
                <form onSubmit={handleTextSubmit} className={styles.form}>
          <textarea
              className={styles.textarea}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Enter your use case description here..."
          />
                    <button type="submit" disabled={loading} className={styles.submitButton}>
                        {loading ? 'Processing...' : 'Submit Text'}
                    </button>
                </form>
            )}

            {selectedTab === 'file' && (
                <form onSubmit={handleFileSubmit} className={styles.form}>
                    <input
                        type="file"
                        accept="application/pdf"
                        onChange={onFileChange}
                        className={styles.fileInput}
                    />
                    <button type="submit" disabled={loading || !file} className={styles.submitButton}>
                        {loading ? 'Processing...' : 'Upload PDF'}
                    </button>
                </form>
            )}

            {error && <p className={styles.error}>{error}</p>}

            {domainObjects.length > 0 && (
                <div className={styles.results}>
                    <h2>Identified Domain Objects:</h2>
                    <ul className={styles.domainList}>
                        {domainObjects.map((obj, index) => (
                            <li key={index}>{obj}</li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
};

export default UseCaseUploader;

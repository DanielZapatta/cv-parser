import { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';

export default function FileUpload({ onTextExtracted, onFileSelected }) {
  const [fileName, setFileName] = useState('');

  const onDrop = useCallback((acceptedFiles) => {
    const file = acceptedFiles[0];
    if (!file) return;
    setFileName(file.name);
    if (onFileSelected) onFileSelected(file);
  }, [onFileSelected]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'application/pdf': ['.pdf'], 'text/plain': ['.txt'] },
    maxFiles: 1,
  });

  return (
    <div
      {...getRootProps()}
      className={`border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-colors ${
        isDragActive
          ? 'border-blue-500 bg-blue-50'
          : 'border-gray-300 hover:border-blue-400 hover:bg-gray-50'
      }`}
    >
      <input {...getInputProps()} />
      <div className="flex flex-col items-center gap-3">
        <svg
          className={`w-12 h-12 ${isDragActive ? 'text-blue-500' : 'text-gray-400'}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={1.5}
            d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
          />
        </svg>
        {fileName ? (
          <div>
            <p className="text-sm font-medium text-gray-700">{fileName}</p>
            <p className="text-xs text-gray-500 mt-1">Click or drag to replace</p>
          </div>
        ) : (
          <div>
            <p className="text-sm font-medium text-gray-700">
              {isDragActive ? 'Drop your CV here' : 'Upload your CV'}
            </p>
            <p className="text-xs text-gray-500 mt-1">PDF or TXT, max 10MB</p>
          </div>
        )}
      </div>
    </div>
  );
}

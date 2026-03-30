import { useState, useRef } from 'react';
import FileUpload from './components/FileUpload';
import ScoreDisplay from './components/ScoreDisplay';
import SkillsHighlight from './components/SkillsHighlight';
import CvPreview from './components/CvPreview';
import OptimizationPlanCard from './components/OptimizationPlanCard';
import { optimizeCv, parsePdf } from './services/api';

const MODELS = ['qwen2.5:7b', 'llama3.2:3b', 'deepseek-r1:7b', 'mistral:7b'];

export default function App() {
  const [cvText, setCvText] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedModel, setSelectedModel] = useState(MODELS[0]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);
  const [activeTab, setActiveTab] = useState('cv');
  const fileInputRef = useRef(null);

  const handleFileSelected = (file) => {
    setSelectedFile(file);
    if (file.type === 'text/plain') {
      const reader = new FileReader();
      reader.onload = (e) => setCvText(e.target.result);
      reader.readAsText(file);
    }
  };

  const handleExtractPdf = async () => {
    if (!selectedFile) return;
    setLoading(true);
    setError('');
    try {
      const data = await parsePdf(selectedFile, selectedModel);
      setCvText(data.rawText || '');
    } catch (err) {
      setError('Failed to extract PDF text. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleOptimize = async () => {
    if (!cvText.trim()) {
      setError('Please provide your CV text.');
      return;
    }
    if (!jobDescription.trim()) {
      setError('Please provide the job description.');
      return;
    }
    setLoading(true);
    setError('');
    setResult(null);

    try {
      const data = await optimizeCv(cvText, jobDescription, selectedModel);
      setResult(data);
      setActiveTab('cv');
    } catch (err) {
      const msg = err.response?.data?.message || err.message || 'Optimization failed';
      setError(`Error: ${msg}. Make sure the backend and Ollama are running.`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
      {/* Header */}
      <header className="bg-white border-b shadow-sm sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
              <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">CV Tailor AI</h1>
              <p className="text-xs text-gray-500">Optimize your CV for any job</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <label className="text-xs text-gray-500">Model:</label>
            <select
              value={selectedModel}
              onChange={(e) => setSelectedModel(e.target.value)}
              className="text-xs border rounded-lg px-2 py-1.5 bg-white"
            >
              {MODELS.map((m) => <option key={m} value={m}>{m}</option>)}
            </select>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        {!result ? (
          /* Input Section */
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Left: CV Input */}
            <div className="space-y-4">
              <div className="bg-white rounded-2xl shadow p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-6 h-6 bg-blue-100 text-blue-700 rounded-full text-xs flex items-center justify-center font-bold">1</span>
                  Your CV
                </h2>

                <FileUpload onFileSelected={handleFileSelected} />

                {selectedFile?.type === 'application/pdf' && (
                  <button
                    onClick={handleExtractPdf}
                    disabled={loading}
                    className="mt-3 w-full text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 py-2 rounded-lg transition-colors"
                  >
                    Extract text from PDF
                  </button>
                )}

                <div className="mt-4">
                  <p className="text-xs text-gray-500 mb-2">Or paste your CV text directly:</p>
                  <textarea
                    value={cvText}
                    onChange={(e) => setCvText(e.target.value)}
                    rows={12}
                    placeholder="Paste your CV/resume text here..."
                    className="w-full border rounded-xl p-3 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                  />
                  <p className="text-xs text-gray-400 mt-1">{cvText.length} characters</p>
                </div>
              </div>
            </div>

            {/* Right: Job Description */}
            <div className="space-y-4">
              <div className="bg-white rounded-2xl shadow p-6">
                <h2 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                  <span className="w-6 h-6 bg-blue-100 text-blue-700 rounded-full text-xs flex items-center justify-center font-bold">2</span>
                  Job Description
                </h2>
                <textarea
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                  rows={20}
                  placeholder="Paste the job description here..."
                  className="w-full border rounded-xl p-3 text-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                />
                <p className="text-xs text-gray-400 mt-1">{jobDescription.length} characters</p>
              </div>
            </div>

            {/* Full width: Generate button + error */}
            <div className="lg:col-span-2">
              {error && (
                <div className="mb-4 bg-red-50 text-red-700 rounded-xl px-4 py-3 text-sm border border-red-200">
                  {error}
                </div>
              )}
              <button
                onClick={handleOptimize}
                disabled={loading || !cvText.trim() || !jobDescription.trim()}
                className="w-full py-4 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-semibold text-base rounded-xl transition-colors flex items-center justify-center gap-3"
              >
                {loading ? (
                  <>
                    <svg className="animate-spin w-5 h-5" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
                    </svg>
                    Optimizing your CV... (this may take a few minutes)
                  </>
                ) : (
                  <>
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                    </svg>
                    Generate Optimized CV
                  </>
                )}
              </button>
            </div>
          </div>
        ) : (
          /* Results Section */
          <div className="space-y-6">
            {/* Back button */}
            <button
              onClick={() => setResult(null)}
              className="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 transition-colors"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
              Back to editor
            </button>

            {/* Score at top */}
            {result.matchingResult && (
              <ScoreDisplay
                score={result.matchingResult.score}
                matchedSkills={result.matchingResult.matchedSkills}
                missingSkills={result.matchingResult.missingSkills}
              />
            )}

            {/* Tabs */}
            <div className="flex gap-2 border-b">
              {['cv', 'plan', 'skills'].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`px-4 py-2 text-sm font-medium capitalize transition-colors border-b-2 -mb-px ${
                    activeTab === tab
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  {tab === 'cv' ? 'Optimized CV' : tab === 'plan' ? 'Optimization Plan' : 'Skills Analysis'}
                </button>
              ))}
            </div>

            {activeTab === 'cv' && <CvPreview cv={result.optimizedCv} />}

            {activeTab === 'plan' && <OptimizationPlanCard plan={result.optimizationPlan} />}

            {activeTab === 'skills' && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <SkillsHighlight
                  skills={result.matchingResult?.matchedSkills}
                  title="✅ Matched Skills"
                />
                <SkillsHighlight
                  skills={result.matchingResult?.missingSkills}
                  title="❌ Missing Skills"
                />
                <SkillsHighlight
                  skills={result.optimizedCv?.skills}
                  title="🚀 Optimized CV Skills"
                />
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

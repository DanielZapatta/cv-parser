import { useRef } from 'react';

export default function CvPreview({ cv }) {
  const previewRef = useRef(null);

  if (!cv) return null;

  const handlePrint = () => {
    const content = previewRef.current;
    if (!content) return;
    const win = window.open('', '_blank');
    win.document.write(`
      <html>
        <head>
          <title>${cv.fullName || 'CV'} - Optimized CV</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 2cm; color: #111; font-size: 12px; }
            h1 { font-size: 22px; margin-bottom: 4px; }
            h2 { font-size: 15px; border-bottom: 1px solid #ccc; padding-bottom: 2px; margin-top: 16px; }
            .contact { color: #555; font-size: 11px; }
            .exp { margin-bottom: 10px; }
            .exp-title { font-weight: bold; }
            .tag { display: inline-block; background: #e0eaff; border-radius: 3px; padding: 1px 6px; margin: 2px; font-size: 10px; }
            ul { margin: 4px 0 4px 16px; padding: 0; }
          </style>
        </head>
        <body>${content.innerHTML}</body>
      </html>
    `);
    win.document.close();
    win.print();
  };

  return (
    <div className="bg-white rounded-2xl shadow">
      <div className="flex items-center justify-between p-4 border-b">
        <h2 className="text-lg font-semibold text-gray-800">Optimized CV Preview</h2>
        <button
          onClick={handlePrint}
          className="flex items-center gap-2 text-sm bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z" />
          </svg>
          Print / Export PDF
        </button>
      </div>

      <div ref={previewRef} className="p-6 space-y-4 text-sm text-gray-800">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{cv.fullName}</h1>
          <p className="text-gray-500 text-xs mt-1">
            {[cv.email, cv.phone, cv.location].filter(Boolean).join(' · ')}
          </p>
        </div>

        {/* Summary */}
        {cv.summary && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-2">Professional Summary</h2>
            <p className="text-gray-700 leading-relaxed">{cv.summary}</p>
          </section>
        )}

        {/* Skills */}
        {cv.skills?.length > 0 && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-2">Skills</h2>
            <div className="flex flex-wrap gap-1.5">
              {cv.skills.map((s, i) => (
                <span key={i} className="bg-blue-50 text-blue-700 text-xs px-2 py-0.5 rounded-full border border-blue-200">
                  {s}
                </span>
              ))}
            </div>
          </section>
        )}

        {/* Experience */}
        {cv.experiences?.length > 0 && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-3">Experience</h2>
            <div className="space-y-4">
              {cv.experiences.map((exp, i) => (
                <div key={i}>
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-semibold text-gray-800">{exp.title}</p>
                      <p className="text-gray-600 text-xs">{exp.company}</p>
                    </div>
                    <p className="text-gray-400 text-xs whitespace-nowrap ml-2">
                      {exp.startDate} – {exp.endDate || 'Present'}
                    </p>
                  </div>
                  {exp.description && (
                    <p className="text-gray-600 mt-1 text-xs leading-relaxed">{exp.description}</p>
                  )}
                  {exp.achievements?.length > 0 && (
                    <ul className="mt-1 space-y-0.5">
                      {exp.achievements.map((a, j) => (
                        <li key={j} className="text-xs text-gray-600 flex gap-1">
                          <span className="text-blue-500 flex-shrink-0">•</span>
                          {a}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Education */}
        {cv.educations?.length > 0 && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-3">Education</h2>
            <div className="space-y-2">
              {cv.educations.map((edu, i) => (
                <div key={i} className="flex justify-between">
                  <div>
                    <p className="font-medium text-gray-800">{edu.degree} {edu.field && `in ${edu.field}`}</p>
                    <p className="text-gray-500 text-xs">{edu.institution}</p>
                  </div>
                  <p className="text-gray-400 text-xs">
                    {edu.startDate} – {edu.endDate || 'Present'}
                  </p>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* Certifications */}
        {cv.certifications?.length > 0 && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-2">Certifications</h2>
            <ul className="space-y-1">
              {cv.certifications.map((c, i) => (
                <li key={i} className="text-xs text-gray-700 flex gap-1">
                  <span className="text-blue-500">•</span>{c}
                </li>
              ))}
            </ul>
          </section>
        )}

        {/* Languages */}
        {cv.languages?.length > 0 && (
          <section>
            <h2 className="text-base font-semibold text-gray-700 border-b pb-1 mb-2">Languages</h2>
            <div className="flex gap-2 flex-wrap">
              {cv.languages.map((l, i) => (
                <span key={i} className="text-xs text-gray-700 bg-gray-100 px-2 py-0.5 rounded">{l}</span>
              ))}
            </div>
          </section>
        )}
      </div>
    </div>
  );
}

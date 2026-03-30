export default function ScoreDisplay({ score, matchedSkills = [], missingSkills = [] }) {
  const pct = Math.round(score || 0);
  const color =
    pct >= 75 ? 'text-green-600' :
    pct >= 50 ? 'text-yellow-600' :
    'text-red-500';

  const strokeColor =
    pct >= 75 ? '#16a34a' :
    pct >= 50 ? '#ca8a04' :
    '#ef4444';

  const radius = 54;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (pct / 100) * circumference;

  return (
    <div className="bg-white rounded-2xl shadow p-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">ATS Match Score</h2>
      <div className="flex items-center gap-6">
        {/* Circular progress */}
        <div className="relative w-32 h-32 flex-shrink-0">
          <svg className="w-32 h-32 -rotate-90" viewBox="0 0 128 128">
            <circle cx="64" cy="64" r={radius} fill="none" stroke="#e5e7eb" strokeWidth="12" />
            <circle
              cx="64"
              cy="64"
              r={radius}
              fill="none"
              stroke={strokeColor}
              strokeWidth="12"
              strokeDasharray={circumference}
              strokeDashoffset={offset}
              strokeLinecap="round"
            />
          </svg>
          <div className="absolute inset-0 flex items-center justify-center">
            <span className={`text-2xl font-bold ${color}`}>{pct}%</span>
          </div>
        </div>

        <div className="flex-1 space-y-3">
          {matchedSkills.length > 0 && (
            <div>
              <p className="text-xs font-semibold text-green-700 mb-1">✅ Matched Skills</p>
              <div className="flex flex-wrap gap-1">
                {matchedSkills.slice(0, 6).map((s, i) => (
                  <span key={`matched-${s}-${i}`} className="text-xs bg-green-100 text-green-800 rounded-full px-2 py-0.5">
                    {s}
                  </span>
                ))}
                {matchedSkills.length > 6 && (
                  <span className="text-xs text-gray-500">+{matchedSkills.length - 6} more</span>
                )}
              </div>
            </div>
          )}

          {missingSkills.length > 0 && (
            <div>
              <p className="text-xs font-semibold text-red-600 mb-1">❌ Missing Skills</p>
              <div className="flex flex-wrap gap-1">
                {missingSkills.slice(0, 6).map((s, i) => (
                  <span key={`missing-${s}-${i}`} className="text-xs bg-red-100 text-red-800 rounded-full px-2 py-0.5">
                    {s}
                  </span>
                ))}
                {missingSkills.length > 6 && (
                  <span className="text-xs text-gray-500">+{missingSkills.length - 6} more</span>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

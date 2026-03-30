export default function OptimizationPlanCard({ plan }) {
  if (!plan) return null;

  return (
    <div className="bg-white rounded-2xl shadow p-6 space-y-4">
      <h2 className="text-lg font-semibold text-gray-800">Optimization Plan</h2>

      {plan.objective && (
        <div>
          <p className="text-xs font-semibold text-gray-500 uppercase mb-1">Objective</p>
          <p className="text-sm text-gray-700">{plan.objective}</p>
        </div>
      )}

      {plan.summaryRewrite && (
        <div>
          <p className="text-xs font-semibold text-gray-500 uppercase mb-1">Rewritten Summary</p>
          <p className="text-sm text-gray-700 bg-blue-50 rounded-lg p-3 border border-blue-100">{plan.summaryRewrite}</p>
        </div>
      )}

      {plan.keywordsToAdd?.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-gray-500 uppercase mb-1">Keywords to Add</p>
          <div className="flex flex-wrap gap-1">
            {plan.keywordsToAdd.map((k, i) => (
              <span key={`keyword-${k}-${i}`} className="bg-yellow-100 text-yellow-800 text-xs px-2 py-0.5 rounded-full">{k}</span>
            ))}
          </div>
        </div>
      )}

      {plan.generalRecommendations?.length > 0 && (
        <div>
          <p className="text-xs font-semibold text-gray-500 uppercase mb-1">Recommendations</p>
          <ul className="space-y-1">
            {plan.generalRecommendations.map((r, i) => (
              <li key={`rec-${i}`} className="text-xs text-gray-700 flex gap-1.5">
                <span className="text-blue-500 flex-shrink-0 mt-0.5">→</span>{r}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

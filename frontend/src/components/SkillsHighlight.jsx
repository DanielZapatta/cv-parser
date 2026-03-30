export default function SkillsHighlight({ skills = [], title = 'Skills' }) {
  return (
    <div className="bg-white rounded-2xl shadow p-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-3">{title}</h2>
      {skills.length === 0 ? (
        <p className="text-sm text-gray-500">No skills found</p>
      ) : (
        <div className="flex flex-wrap gap-2">
          {skills.map((skill, i) => (
            <span
              key={`${skill}-${i}`}
              className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-1 rounded-full"
            >
              {skill}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}

// src/app/components/common/CodeParticles.tsx
"use client";

import React from "react";

interface CodeParticleProps {
  className?: string;
}

const CodeParticles: React.FC<CodeParticleProps> = ({ className = "" }) => {
  return (
    <div
      className={`absolute top-0 left-0 w-full h-full overflow-hidden opacity-10 pointer-events-none ${className}`}
    >
      {[
        { top: "5%", left: "10%", rotate: "15deg", text: "function()" },
        { top: "15%", left: "85%", rotate: "-10deg", text: "const data = []" },
        { top: "25%", left: "20%", rotate: "5deg", text: "for(let i=0;)" },
        { top: "30%", left: "60%", rotate: "-20deg", text: "if(isValid)" },
        { top: "45%", left: "15%", rotate: "12deg", text: "return result" },
        { top: "50%", left: "75%", rotate: "-5deg", text: "{ }" },
        { top: "60%", left: "40%", rotate: "8deg", text: "=> {}" },
        { top: "70%", left: "90%", rotate: "-15deg", text: "import" },
        { top: "75%", left: "30%", rotate: "20deg", text: "export" },
        { top: "85%", left: "65%", rotate: "-8deg", text: "class" },
        { top: "10%", left: "40%", rotate: "22deg", text: "async/await" },
        { top: "35%", left: "80%", rotate: "-25deg", text: "try/catch" },
        { top: "55%", left: "5%", rotate: "18deg", text: ".then()" },
        { top: "65%", left: "55%", rotate: "-12deg", text: "useState()" },
        { top: "80%", left: "15%", rotate: "10deg", text: "useEffect()" },
        { top: "20%", left: "50%", rotate: "-18deg", text: "<></>" },
        { top: "40%", left: "35%", rotate: "15deg", text: "npm install" },
        { top: "90%", left: "45%", rotate: "-5deg", text: "git commit" },
        { top: "15%", left: "25%", rotate: "8deg", text: "docker run" },
        { top: "70%", left: "70%", rotate: "-22deg", text: "console.log()" },
      ].map((particle, i) => (
        <div
          key={i}
          className="absolute text-gray-800 dark:text-gray-200 text-opacity-30 font-mono text-sm"
          style={{
            top: particle.top,
            left: particle.left,
            transform: `rotate(${particle.rotate})`,
          }}
        >
          {particle.text}
        </div>
      ))}
    </div>
  );
};

export default CodeParticles;

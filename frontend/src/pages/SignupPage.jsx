import React from "react";
import Signup from "../components/Signup";
function SignupPage() {
  return (
    <div>
      <div className="grid grid-cols-12 min-w-screen">
        <div className="col-span-4">
          <div className="bg-black hero min-h-screen ">
            <div className="hero-content">
              <h1 className="px-2 text-[7rem] text-white font-thin">
                Weclome Aboard{" "}
              </h1>
            </div>
          </div>
        </div>
        <div className="col-span-8">
          <Signup />
        </div>
      </div>
    </div>
  );
}

export default SignupPage;

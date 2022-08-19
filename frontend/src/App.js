import LoginPage from "./pages/LoginPage";
import "./App.css";
import RouteGuard from "./components/RouteGuard";
import Homepage from "./pages/Homepage";
import { ToastContainer } from "react-toastify";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SignupPage from "./pages/SignupPage";
import Navbar from "./components/Navbar";
import AllTweets from "./pages/AllTweets";
function App() {
  return (
    <>
      <ToastContainer />
      <Navbar />
      <Router>
        <Routes>
          <Route
            exact
            path="/"
            element={
              <RouteGuard>
                <Homepage />
              </RouteGuard>
            }
          />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/tweets" element={<AllTweets />} />
        </Routes>
      </Router>
    </>
  );
}

export default App;
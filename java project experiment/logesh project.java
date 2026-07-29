A.2  src/types.ts
export type UserRole = 'Student' | 'Faculty';
 
export type AppView = 'login' | 'dashboard' | 'tasks' | 'team' | 'risks' | 'faculty' | 'schema';
 
export interface PastContribution {
  project_name: string;
  role: string;
  year: string;
  description: string;
  commits_count?: number;
}
 
export interface Student {
  id: number;
  name: string;
  email: string;
  role: 'Student' | 'Team Lead';
  gpa?: string;
  year?: string;
  bio?: string;
  skills: string[];
  avatarUrl: string;
  status: 'Available' | 'Interviewing' | 'Assigned';
  capacityPct: number; // e.g. 85%
  workloadStatus: 'Underloaded' | 'Balanced' | 'Overloaded';
  lastActive: string;
  pastContributions?: PastContribution[];
}
 
export interface Faculty {
  id: number;
  name: string;
  email: string;
  department?: string;
  avatarUrl?: string;
}
 
export interface Team {
  id: number;
  team_name: string;
  project_subtitle?: string;
  created_by?: number;
  progress_pct: number;
  high_risks_count: number;
  med_risks_count: number;
  team_size: number;
  next_milestone: string;
  sprint_velocity: string;
  member_ids: number[];
  target_deadline?: string;
}
 
export interface Project {
  id: number;
  team_id: number;
  project_name: string;
  description: string;
  status: 'Active' | 'Planning' | 'Completed' | 'On Hold';
  faculty_advisor_id?: number;
  target_deadline?: string;
}
 
export interface ExternalUrl {
  label: string;
  url: string;
}
 
export interface Task {
  id: number;
  project_id: number;
  title: string;
  description: string;
  assigned_to: number; // Student id
  assigned_name?: string;
  assigned_avatar?: string;
  status: 'To Do' | 'In Progress' | 'Done';
  priority: 'Low' | 'Medium' | 'High';
  risk_level: 'On Track' | 'Medium Risk' | 'High Risk';
  due_date: string;
  created_date: string;
  ideal_progress_pct: number;
  actual_progress_pct: number;
  category_tag?: string; // e.g. DEV, DESIGN, DOCS
  delay_text?: string; // e.g. "4 days behind"
  estimated_hours?: number;
  progress_history?: { date: string; progress: number }[];
  external_urls?: ExternalUrl[];
  depends_on_task_id?: number;
}
 
export interface ProjectFile {
  id: number;
  project_id: number;
  filename: string;
  version: string;
  uploaded_by: number;
  uploaded_by_name?: string;
  uploaded_date: string;
  size?: string;
}
 
export interface ContributionLog {
  id: number;
  student_id: number;
  student_name: string;
  project_id: number;
  action_type: string;
  details: string;
  timestamp: string;
  badge_color?: string;
}
 
export interface RiskInsightAlert {
  id: number;
  title: string;
  assigned_to_name: string;
  group_or_phase: string;
  risk_badge: string;
  actionable_tip: string;
  severity: 'high' | 'medium' | 'low';
}
 
export interface LowContributionUser {
  id: number;
  student_id: number;
  name: string;
  risk_level: 'High Risk' | 'Medium Risk' | 'Info Only';
  updates_count: number;
  last_login: string;
}
 

A.3  src/App.tsx
import React, { useState } from 'react';
import { AppView, UserRole, Task, Student, Team, RiskInsightAlert, LowContributionUser, ContributionLog } from './types';
import {
  INITIAL_STUDENTS,
  INITIAL_TEAMS,
  INITIAL_FACULTY,
  INITIAL_TASKS,
  INITIAL_RISK_ALERTS,
  LOW_CONTRIBUTION_USERS,
  INITIAL_ACTIVITY_LOGS,
} from './data/mockData';
 
import { TopNavBar } from './components/TopNavBar';
import { SideNavBar } from './components/SideNavBar';
import { AuthScreen } from './components/AuthScreen';
import { StudentDashboardView } from './components/StudentDashboardView';
import { TaskBoardView } from './components/TaskBoardView';
import { TeamFormationView } from './components/TeamFormationView';
import { RiskInsightsView } from './components/RiskInsightsView';
import { FacultyOverviewView } from './components/FacultyOverviewView';
import { MySQLSchemaView } from './components/MySQLSchemaView';
import { NewProjectModal } from './components/NewProjectModal';
 
export default function App() {
  const [currentView, setCurrentView] = useState<AppView>('dashboard');
  const [userRole, setUserRole] = useState<UserRole>('Student');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [isNewProjectModalOpen, setIsNewProjectModalOpen] = useState(false);
 
  // App State
  const [students] = useState<Student[]>(INITIAL_STUDENTS);
  const [teams, setTeams] = useState<Team[]>(INITIAL_TEAMS);
  const [tasks, setTasks] = useState<Task[]>(INITIAL_TASKS);
  const [riskAlerts] = useState<RiskInsightAlert[]>(INITIAL_RISK_ALERTS);
  const [lowContribUsers] = useState<LowContributionUser[]>(LOW_CONTRIBUTION_USERS);
  const [activityLogs, setActivityLogs] = useState<ContributionLog[]>(INITIAL_ACTIVITY_LOGS);
 
  // Auth Handle
  const handleLoginSuccess = (role: UserRole) => {
    setUserRole(role);
    if (role === 'Faculty') {
      setCurrentView('faculty');
    } else {
      setCurrentView('dashboard');
    }
  };
 
  // Add Task
  const handleAddTask = (
    newTaskData: Omit<Task, 'id' | 'created_date' | 'ideal_progress_pct' | 'actual_progress_pct'>
  ) => {
    const newTask: Task = {
      ...newTaskData,
      id: Date.now(),
      created_date: new Date().toISOString().split('T')[0],
      ideal_progress_pct: 100,
      actual_progress_pct: 0,
    };
 
    setTasks((prev) => [newTask, ...prev]);
 
    // Log contribution
    const newLog: ContributionLog = {
      id: Date.now(),
      student_id: 0,
      student_name: newTaskData.assigned_name || 'Student',
      project_id: 1,
      action_type: 'created task',
      details: `Created task "${newTask.title}" assigned to ${newTask.assigned_name}.`,
      timestamp: 'JUST NOW',
      badge_color: 'bg-primary',
    };
    setActivityLogs((prev) => [newLog, ...prev]);
  };
 
  // Update Task Status
  const handleUpdateTaskStatus = (taskId: number, newStatus: 'To Do' | 'In Progress' | 'Done') => {
    setTasks((prev) =>
      prev.map((t) => (t.id === taskId ? { ...t, status: newStatus } : t))
    );
  };
 
  // Update Task Progress Percentage
  const handleUpdateTaskProgress = (taskId: number, newProgressPct: number) => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.id === taskId) {
          const history = t.progress_history
            ? [...t.progress_history]
            : [
                { date: 'Start', progress: 0 },
                { date: 'Initial', progress: t.actual_progress_pct },
              ];
          history.push({
            date: `Rev ${history.length + 1}`,
            progress: newProgressPct,
          });
          return {
            ...t,
            actual_progress_pct: newProgressPct,
            progress_history: history,
          };
        }
        return t;
      })
    );
  };
 
  // Attach External Link to Task
  const handleAttachTaskUrl = (taskId: number, label: string, url: string) => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.id === taskId) {
          const existing = t.external_urls || [];
          return {
            ...t,
            external_urls: [...existing, { label, url }],
          };
        }
        return t;
      })
    );
  };
 
  // Invite Student to Team
  const handleInviteStudent = (student: Student) => {
    const newLog: ContributionLog = {
      id: Date.now(),
      student_id: student.id,
      student_name: 'Harish Narayanan',
      project_id: 1,
      action_type: 'invited team member',
      details: `Sent invitation to ${student.name} (${student.skills.slice(0, 2).join(', ')}).`,
      timestamp: 'JUST NOW',
      badge_color: 'bg-secondary',
    };
    setActivityLogs((prev) => [newLog, ...prev]);
  };
 
  // Auto-Balance Workload
  const handleRebalanceWorkload = () => {
    setTasks((prev) =>
      prev.map((t) => {
        if (t.assigned_name === 'Jordan Smith' && t.status === 'To Do') {
          return {
            ...t,
            assigned_name: 'Sarah Johnson',
            assigned_avatar:
              'https://lh3.googleusercontent.com/aida-public/AB6AXuBCYd9K34eDiJARnmPPnCufamTIMRWAQPthbR_beGkxNDqGEQtj4GjnXmSVM2IGDmVEBzmLBF7DymSsXaBtgV_f_CYCdoz9V34Fqf9hZ49C8xSOsSuuBbx2g-2j3czd9nN1PN-gWcp6goJMkkyktQ-dEmg-dxMN3qeiVHe54B3dDQiMGHxCIgf8KT0UXaCa7DJ0fBcD4YPrhO_TkkUxdkILzWkDOqJHhSHPdD-qszgBpLnMtkSZW_PQxLOoomTGG21kd7x3neO12jKY',
            risk_level: 'On Track',
          };
        }
        return t;
      })
    );
  };
 
  // Create New Team
  const handleCreateTeam = (teamName: string, projectTitle: string, targetDeadline?: string) => {
    const newTeam: Team = {
      id: Date.now(),
      team_name: teamName,
      project_subtitle: projectTitle,
      target_deadline: targetDeadline || '2026-11-15',
      progress_pct: 10,
      high_risks_count: 0,
      med_risks_count: 0,
      team_size: 1,
      next_milestone: 'Project Kickoff',
      sprint_velocity: '0 pts/wk',
      member_ids: [0],
    };
    setTeams((prev) => [newTeam, ...prev]);
  };
 
  // If in Login mode
  if (currentView === 'login') {
    return <AuthScreen onLoginSuccess={handleLoginSuccess} />;
  }
 
  return (
    <div className="min-h-screen bg-[#f9f9ff] text-[#141b2b] flex flex-col font-['Inter',sans-serif]">
      {/* Top Navbar */}
      <TopNavBar
        currentView={currentView}
        setCurrentView={setCurrentView}
        userRole={userRole}
        setUserRole={setUserRole}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onOpenNewProject={() => setIsNewProjectModalOpen(true)}
      />
 
      <div className="flex flex-1 pt-16">
        {/* Left Sidebar (Desktop) */}
        <SideNavBar
          currentView={currentView}
          setCurrentView={setCurrentView}
          userRole={userRole}
          onOpenNewProject={() => setIsNewProjectModalOpen(true)}
        />
 
        {/* Main Content View Container */}
        <main className="flex-1 md:ml-64 p-4 md:p-8 pb-20 md:pb-12 max-w-7xl mx-auto w-full transition-all">
          {currentView === 'dashboard' && (
            <StudentDashboardView
              tasks={tasks}
              activityLogs={activityLogs}
              onNavigateView={setCurrentView}
              activeTeam={teams[0]}
            />
          )}
 
          {currentView === 'tasks' && (
            <TaskBoardView
              tasks={tasks}
              onAddTask={handleAddTask}
              onUpdateTaskStatus={handleUpdateTaskStatus}
              onUpdateTaskProgress={handleUpdateTaskProgress}
              onAttachUrl={handleAttachTaskUrl}
              searchQuery={searchQuery}
            />
          )}
 
          {currentView === 'team' && (
            <TeamFormationView
              students={students}
              onInviteStudent={handleInviteStudent}
            />
          )}
 
          {currentView === 'risks' && (
            <RiskInsightsView
              alerts={riskAlerts}
              lowContributionUsers={lowContribUsers}
              onRebalanceWorkload={handleRebalanceWorkload}
            />
          )}
 
          {currentView === 'faculty' && (
            <FacultyOverviewView
              teams={teams}
              faculty={INITIAL_FACULTY[0]}
            />
          )}
 
          {currentView === 'schema' && <MySQLSchemaView />}
        </main>
      </div>
 
      {/* Mobile Bottom Navigation Bar */}
      <div className="md:hidden fixed bottom-0 left-0 w-full bg-white border-t border-[#c7c4d8] flex items-center justify-around py-2 px-1 z-50 text-[10px] font-semibold text-[#464555]">
        <button
          onClick={() => setCurrentView('dashboard')}
          className={`flex flex-col items-center gap-0.5 ${
            currentView === 'dashboard' ? 'text-[#3525cd]' : ''
          }`}
        >
          <span className="material-symbols-outlined text-lg">dashboard</span>
          <span>Home</span>
        </button>
 
        <button
          onClick={() => setCurrentView('tasks')}
          className={`flex flex-col items-center gap-0.5 ${
            currentView === 'tasks' ? 'text-[#3525cd]' : ''
          }`}
        >
          <span className="material-symbols-outlined text-lg">assignment</span>
          <span>Tasks</span>
        </button>
 
        <button
          onClick={() => setCurrentView('team')}
          className={`flex flex-col items-center gap-0.5 ${
            currentView === 'team' ? 'text-[#3525cd]' : ''
          }`}
        >
          <span className="material-symbols-outlined text-lg">group</span>
          <span>Team</span>
        </button>
 
        <button
          onClick={() => setCurrentView('risks')}
          className={`flex flex-col items-center gap-0.5 ${
            currentView === 'risks' ? 'text-[#3525cd]' : ''
          }`}
        >
          <span className="material-symbols-outlined text-lg">warning</span>
          <span>Risks</span>
        </button>
 
        <button
          onClick={() => setCurrentView('schema')}
          className={`flex flex-col items-center gap-0.5 text-[#3525cd] font-bold`}
        >
          <span className="material-symbols-outlined text-lg">database</span>
          <span>MySQL</span>
        </button>
      </div>
 
      {/* Initialize New Project Modal */}
      <NewProjectModal
        isOpen={isNewProjectModalOpen}
        onClose={() => setIsNewProjectModalOpen(false)}
        onCreateTeam={handleCreateTeam}
      />
    </div>
  );
}
 

A.4  src/components/AuthScreen.tsx
import React, { useState } from 'react';
import { UserRole } from '../types';
 
interface AuthScreenProps {
  onLoginSuccess: (role: UserRole) => void;
}
 
export const AuthScreen: React.FC<AuthScreenProps> = ({ onLoginSuccess }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [selectedRole, setSelectedRole] = useState<UserRole>('Student');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('harish.narayanan@university.edu');
  const [password, setPassword] = useState('password123');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
 
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
 
    setTimeout(() => {
      const msg = isLogin ? 'Signing you in...' : 'Account created successfully!';
      setToastMessage(msg);
      setIsLoading(false);
 
      setTimeout(() => {
        setToastMessage(null);
        onLoginSuccess(selectedRole);
      }, 1200);
    }, 800);
  };
 
  return (
    <div className="min-h-screen bg-[#f9f9ff] flex flex-col justify-center items-center p-4 relative overflow-hidden">
      {/* Background Glow Orbs */}
      <div className="fixed inset-0 -z-10 pointer-events-none opacity-40">
        <div className="absolute top-[-10%] right-[-5%] w-96 h-96 bg-[#e2dfff] rounded-full blur-[100px]"></div>
        <div className="absolute bottom-[-10%] left-[-5%] w-96 h-96 bg-[#6ffbbe] rounded-full blur-[100px]"></div>
      </div>
 
      <main className="w-full max-w-[440px] space-y-6">
        {/* Brand Header */}
        <div className="text-center space-y-2">
          <div className="flex justify-center items-center gap-2 mb-3">
            <div className="w-10 h-10 bg-[#4f46e5] rounded-xl flex items-center justify-center shadow-md">
              <span className="material-symbols-outlined text-[#dad7ff] text-2xl" style={{ fontVariationSettings: "'FILL' 1" }}>
                hub
              </span>
            </div>
            <h1 className="font-['Inter'] font-black text-2xl text-[#3525cd] tracking-tight">CollabSphere</h1>
          </div>
          <h2 className="font-['Inter'] font-semibold text-xl text-[#141b2b]">
            {isLogin ? 'Welcome back' : 'Create Account'}
          </h2>
          <p className="font-['Inter'] text-sm text-[#464555]">
            {isLogin
              ? 'Access your academic workspace and team projects.'
              : 'Join CollabSphere to start working with your team.'}
          </p>
        </div>
 
        {/* Auth Card */}
        <div className="bg-[#ffffff] border border-[#c7c4d8] rounded-xl p-8 shadow-xs">
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Role Selector */}
            <div className="space-y-2">
              <label className="block text-sm font-semibold text-[#464555]">Select Role</label>
              <div className="relative flex w-full bg-[#e9edff] p-1 rounded-lg border border-[#c7c4d8]">
                <div
                  className={`absolute left-1 top-1 bottom-1 w-[calc(50%-4px)] bg-white rounded-md shadow-xs transition-transform duration-300 ease-out ${
                    selectedRole === 'Faculty' ? 'translate-x-full' : 'translate-x-0'
                  }`}
                ></div>
                <button
                  type="button"
                  onClick={() => setSelectedRole('Student')}
                  className={`relative z-10 flex-1 py-1.5 font-semibold text-sm transition-colors ${
                    selectedRole === 'Student' ? 'text-[#3525cd]' : 'text-[#464555]'
                  }`}
                >
                  Student
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedRole('Faculty')}
                  className={`relative z-10 flex-1 py-1.5 font-semibold text-sm transition-colors ${
                    selectedRole === 'Faculty' ? 'text-[#3525cd]' : 'text-[#464555]'
                  }`}
                >
                  Faculty
                </button>
              </div>
            </div>
 
            {/* Registration Name Field */}
            {!isLogin && (
              <div className="space-y-1.5">
                <label className="block text-sm font-semibold text-[#141b2b]">Full Name</label>
                <div className="relative">
                  <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-[#464555] text-lg">
                    person
                  </span>
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="Enter your full name"
                    className="w-full pl-10 pr-4 py-2.5 bg-white border border-[#777587] rounded-lg text-sm text-[#141b2b] focus:outline-hidden focus:ring-2 focus:ring-[#3525cd] focus:border-transparent placeholder-[#777587]"
                  />
                </div>
              </div>
            )}
 
            {/* Email Field */}
            <div className="space-y-1.5">
              <label className="block text-sm font-semibold text-[#141b2b]">Email Address</label>
              <div className="relative">
                <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-[#464555] text-lg">
                  mail
                </span>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@university.edu"
                  className="w-full pl-10 pr-4 py-2.5 bg-white border border-[#777587] rounded-lg text-sm text-[#141b2b] focus:outline-hidden focus:ring-2 focus:ring-[#3525cd] focus:border-transparent placeholder-[#777587]"
                />
              </div>
            </div>
 
            {/* Password Field */}
            <div className="space-y-1.5">
              <div className="flex justify-between items-center">
                <label className="block text-sm font-semibold text-[#141b2b]">Password</label>
                {isLogin && (
                  <a href="#forgot" onClick={(e) => e.preventDefault()} className="text-xs font-semibold text-[#3525cd] hover:underline">
                    Forgot password?
                  </a
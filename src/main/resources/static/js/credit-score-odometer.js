/**
 * Credit Score Odometer JavaScript
 * Creates an animated half-circle gauge for displaying credit scores
 */

class CreditScoreOdometer {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.options = {
            minScore: 300,
            maxScore: 850,
            animationDuration: 2000,
            showDetails: true,
            ...options
        };
        
        this.currentScore = 0;
        this.currentGrade = '';
        this.needle = null;
        this.scoreDisplay = null;
        this.gradeDisplay = null;
        
        this.init();
    }
    
    init() {
        if (!this.container) {
            console.error('Credit Score Odometer: Container not found');
            return;
        }
        
        this.createOdometerStructure();
        this.createMarks();
    }
    
    createOdometerStructure() {
        this.container.innerHTML = `
            <div class="credit-score-container">
                <h2 class="credit-score-title">Credit Score</h2>
                <div class="odometer-container">
                    <div class="odometer-base"></div>
                    <div class="odometer-overlay"></div>
                    <div class="odometer-marks" id="odometer-marks"></div>
                    <div class="odometer-needle" id="credit-needle"></div>
                    <div class="odometer-center"></div>
                </div>
                <div class="score-display">
                    <div class="score-number" id="score-number">--</div>
                    <div class="score-grade" id="score-grade">Loading...</div>
                </div>
                ${this.options.showDetails ? this.createDetailsHTML() : ''}
            </div>
        `;
        
        this.needle = document.getElementById('credit-needle');
        this.scoreDisplay = document.getElementById('score-number');
        this.gradeDisplay = document.getElementById('score-grade');
    }
    
    createDetailsHTML() {
        return `
            <div class="score-details" id="score-details" style="display: none;">
                <div class="score-detail-item">
                    <h4>Last Updated</h4>
                    <span id="last-updated">--</span>
                </div>
                <div class="score-detail-item">
                    <h4>Score Range</h4>
                    <span>300 - 850</span>
                </div>
                <div class="score-detail-item">
                    <h4>Your Position</h4>
                    <span id="score-position">--</span>
                </div>
            </div>
        `;
    }
    
    createMarks() {
        const marksContainer = document.getElementById('odometer-marks');
        if (!marksContainer) return;
        
        // Create score marks from 300 to 850
        const scores = [300, 400, 500, 600, 700, 800, 850];
        
        scores.forEach(score => {
            const angle = this.scoreToAngle(score);
            const mark = document.createElement('div');
            mark.className = 'odometer-mark major';
            mark.style.transform = `rotate(${angle}deg)`;
            mark.style.left = '50%';
            mark.style.bottom = '0';
            mark.style.transformOrigin = 'bottom center';
            marksContainer.appendChild(mark);
            
            // Add score labels
            const label = document.createElement('div');
            label.className = 'odometer-label';
            label.textContent = score;
            
            // Calculate label position - corrected positioning
            // Convert angle to radians for trigonometric calculations
            const labelAngleRad = (angle + 90) * Math.PI / 180;
            const radius = 45;
            // Fixed coordinate calculation: use cos for x and sin for y to match the rotation
            const x = 50 + (Math.cos(labelAngleRad) * radius);
            const y = 85 - (Math.sin(labelAngleRad) * radius);

            label.style.left = `${x}%`;
            label.style.top = `${y}%`;
            label.style.transform = 'translate(-50%, -50%)';
            marksContainer.appendChild(label);
        });
        
        // Add minor marks
        for (let score = 350; score <= 800; score += 50) {
            if (scores.includes(score)) continue;
            
            const angle = this.scoreToAngle(score);
            const mark = document.createElement('div');
            mark.className = 'odometer-mark';
            mark.style.transform = `rotate(${angle}deg)`;
            mark.style.left = '50%';
            mark.style.bottom = '0';
            mark.style.transformOrigin = 'bottom center';
            marksContainer.appendChild(mark);
        }
    }
    
    scoreToAngle(score) {
        // Convert score to angle (90 to -90 degrees) - reversed to put low scores on left, high on right
        const normalized = (score - this.options.minScore) / (this.options.maxScore - this.options.minScore);
        return 90 - (normalized * 180);
    }
    
    getScoreGrade(score) {
        if (score >= 800) return { grade: 'Excellent', class: 'grade-excellent' };
        if (score >= 740) return { grade: 'Very Good', class: 'grade-very-good' };
        if (score >= 670) return { grade: 'Good', class: 'grade-good' };
        if (score >= 580) return { grade: 'Fair', class: 'grade-fair' };
        return { grade: 'Poor', class: 'grade-poor' };
    }
    
    getScorePosition(score) {
        const percentage = ((score - this.options.minScore) / (this.options.maxScore - this.options.minScore)) * 100;
        if (percentage >= 90) return 'Top 10%';
        if (percentage >= 75) return 'Top 25%';
        if (percentage >= 50) return 'Top 50%';
        if (percentage >= 25) return 'Bottom 50%';
        return 'Bottom 25%';
    }
    
    updateScore(scoreData, animate = true) {
        if (!scoreData || typeof scoreData.creditScore !== 'number') {
            console.error('Invalid score data provided');
            return;
        }
        
        const score = Math.max(this.options.minScore, Math.min(this.options.maxScore, scoreData.creditScore));
        const gradeInfo = this.getScoreGrade(score);
        
        // Update displays
        if (animate) {
            this.animateScoreChange(score, gradeInfo);
        } else {
            this.setScore(score, gradeInfo);
        }
        
        // Update details if available
        if (this.options.showDetails) {
            this.updateDetails(scoreData, score);
        }
        
        this.currentScore = score;
        this.currentGrade = gradeInfo.grade;
    }
    
    animateScoreChange(targetScore, gradeInfo) {
        const startScore = this.currentScore || this.options.minScore;
        const startTime = Date.now();
        
        const animate = () => {
            const elapsed = Date.now() - startTime;
            const progress = Math.min(elapsed / this.options.animationDuration, 1);
            
            // Easing function for smooth animation
            const easeProgress = 1 - Math.pow(1 - progress, 3);
            
            const currentScore = startScore + (targetScore - startScore) * easeProgress;
            const angle = this.scoreToAngle(currentScore);
            
            // Update needle position
            if (this.needle) {
                this.needle.style.transform = `translateX(-50%) rotate(${angle}deg)`;
            }
            
            // Update score display
            if (this.scoreDisplay) {
                this.scoreDisplay.textContent = Math.round(currentScore);
            }
            
            if (progress < 1) {
                requestAnimationFrame(animate);
            } else {
                // Animation complete, set final values
                this.setScore(targetScore, gradeInfo);
            }
        };
        
        requestAnimationFrame(animate);
    }
    
    setScore(score, gradeInfo) {
        const angle = this.scoreToAngle(score);
        
        if (this.needle) {
            this.needle.style.transform = `translateX(-50%) rotate(${angle}deg)`;
        }
        
        if (this.scoreDisplay) {
            this.scoreDisplay.textContent = score;
        }
        
        if (this.gradeDisplay) {
            this.gradeDisplay.textContent = gradeInfo.grade;
            this.gradeDisplay.className = `score-grade ${gradeInfo.class}`;
        }
    }
    
    updateDetails(scoreData, score) {
        const detailsContainer = document.getElementById('score-details');
        if (!detailsContainer) return;
        
        detailsContainer.style.display = 'flex';
        
        // Update last updated
        const lastUpdatedEl = document.getElementById('last-updated');
        if (lastUpdatedEl && scoreData.lastUpdated) {
            const date = new Date(scoreData.lastUpdated);
            lastUpdatedEl.textContent = date.toLocaleDateString();
        }
        
        // Update score position
        const positionEl = document.getElementById('score-position');
        if (positionEl) {
            positionEl.textContent = this.getScorePosition(score);
        }
    }
    
    showLoading() {
        if (this.scoreDisplay) {
            this.scoreDisplay.innerHTML = '<div class="score-loading"></div>';
        }
        if (this.gradeDisplay) {
            this.gradeDisplay.textContent = 'Loading...';
            this.gradeDisplay.className = 'score-grade';
        }
    }
    
    showError(message = 'Unable to load credit score') {
        if (this.scoreDisplay) {
            this.scoreDisplay.textContent = '--';
        }
        if (this.gradeDisplay) {
            this.gradeDisplay.textContent = message;
            this.gradeDisplay.className = 'score-grade grade-poor';
        }
    }
    
    // Public method to fetch and display credit score
    async fetchAndDisplayScore(customerId) {
        try {
            this.showLoading();
            
            const response = await fetch(`/api/banking/credit-score/${customerId}`);
            
            if (response.ok) {
                const scoreData = await response.json();
                this.updateScore(scoreData);
            } else if (response.status === 404) {
                // No credit score found, show default
                this.updateScore({ creditScore: 600, scoreGrade: 'Fair' });
            } else {
                throw new Error('Failed to fetch credit score');
            }
        } catch (error) {
            console.error('Error fetching credit score:', error);
            this.showError();
        }
    }
}

// Utility function to initialize credit score odometer
function initializeCreditScoreOdometer(containerId, customerId, options = {}) {
    const odometer = new CreditScoreOdometer(containerId, options);
    
    // Auto-fetch score if customerId is provided
    if (customerId) {
        odometer.fetchAndDisplayScore(customerId);
    }
    
    return odometer;
}

// Export for use in other scripts
window.CreditScoreOdometer = CreditScoreOdometer;
window.initializeCreditScoreOdometer = initializeCreditScoreOdometer;